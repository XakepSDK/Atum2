package com.teammetallurgy.atum.entity.efreet;

import com.teammetallurgy.atum.Atum;
import com.teammetallurgy.atum.entity.ITexture;
import com.teammetallurgy.atum.entity.ai.goal.OpenAnyDoorGoal;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.UUID;

public abstract class EfreetBaseEntity extends AgeableEntity implements ITexture {
    private static final DataParameter<Integer> VARIANT = EntityDataManager.createKey(EfreetBaseEntity.class, DataSerializers.VARINT);
    private String texturePath;
    protected int angerLevel;
    protected UUID angerTargetUUID;

    public EfreetBaseEntity(EntityType<? extends EfreetBaseEntity> entityType, World world) {
        super(entityType, world);
        ((GroundPathNavigator) this.getNavigator()).setBreakDoors(true);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(2, new OpenAnyDoorGoal(this, true, false));
        this.goalSelector.addGoal(4, new MoveTowardsRestrictionGoal(this, 0.6D));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.addGoal(6, new LookRandomlyGoal(this));
        this.applyEntityAI();
    }

    protected void getSuperGoals() {
        super.registerGoals();
    }

    protected void applyEntityAI() {
        this.targetSelector.addGoal(1, new EfreetBaseEntity.AIHurtByAggressor(this));
        this.targetSelector.addGoal(2, new EfreetBaseEntity.AITargetAggressor(this));
    }

    public static AttributeModifierMap.MutableAttribute getBaseAttributes() {
        return MobEntity.func_233666_p_().createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.25D).createMutableAttribute(Attributes.ATTACK_DAMAGE);
    }

    @Override
    protected void registerData() {
        super.registerData();
        if (this.hasSkinVariants()) {
            this.dataManager.register(VARIANT, 0);
        }
    }

    protected int getVariantAmount() {
        return 3;
    }

    protected boolean hasSkinVariants() {
        return this.getVariantAmount() > 0;
    }

    protected void setVariant(int variant) {
        this.dataManager.set(VARIANT, variant);
        this.texturePath = null;
    }

    private int getVariant() {
        return this.dataManager.get(VARIANT);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public String getTexture() {
        if (this.texturePath == null) {
            String entityName = Objects.requireNonNull(this.getType().getRegistryName()).getPath();
            if (this.hasSkinVariants()) {
                this.texturePath = new ResourceLocation(Atum.MOD_ID, "textures/entity/" + entityName + "_" + this.getVariant()) + ".png";
            } else {
                this.texturePath = new ResourceLocation(Atum.MOD_ID, "textures/entity/" + entityName) + ".png";
            }
        }
        return this.texturePath;
    }

    @Override
    @Nonnull
    public CreatureAttribute getCreatureAttribute() {
        return CreatureAttribute.UNDEFINED;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_ILLUSIONER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(@Nonnull DamageSource damageSource) {
        return SoundEvents.ENTITY_ILLUSIONER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_PILLAGER_DEATH;
    }

    @Override
    @Nullable
    public ILivingEntityData onInitialSpawn(@Nonnull IServerWorld world, @Nonnull DifficultyInstance difficulty, @Nonnull SpawnReason spawnReason, @Nullable ILivingEntityData livingdata, @Nullable CompoundNBT nbt) {
        livingdata = super.onInitialSpawn(world, difficulty, spawnReason, livingdata, nbt);

        this.setEquipmentBasedOnDifficulty(difficulty);
        this.setCanPickUpLoot(this.rand.nextFloat() < 0.55F * difficulty.getClampedAdditionalDifficulty());

        if (this.hasSkinVariants()) {
            final int variant = MathHelper.nextInt(this.rand, 0, getVariantAmount());
            this.setVariant(variant);
        }
        return livingdata;
    }

    @Override
    protected void setEquipmentBasedOnDifficulty(@Nonnull DifficultyInstance difficulty) {
        //Don't use for now, might do something with it later
    }

    @Override
    public void tick() {
        super.tick();
        if (this.world.isRemote && this.dataManager.isDirty()) {
            this.dataManager.setClean();
            this.texturePath = null;
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean canRenderOnFire() {
        return this.isAngry();
    }

    @Override
    public boolean canDespawn(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    protected void updateAITasks() {
        LivingEntity revengeTarget = this.getRevengeTarget();
        if (this.isAngry()) {
            --this.angerLevel;
            LivingEntity livingEntity = revengeTarget != null ? revengeTarget : this.getAttackTarget();
            if (!this.isAngry() && livingEntity != null) {
                if (!this.canEntityBeSeen(livingEntity)) {
                    this.setRevengeTarget(null);
                    this.setAttackTarget(null);
                } else {
                    this.angerLevel = this.getAngryAmount();
                }
            }
        }

        if (this.isAngry() && this.angerTargetUUID != null && revengeTarget == null) {
            PlayerEntity player = this.world.getPlayerByUuid(this.angerTargetUUID);
            this.setRevengeTarget(player);
            this.attackingPlayer = player;
            this.recentlyHit = this.getRevengeTimer();
        }
        super.updateAITasks();
    }

    @Override
    public boolean attackEntityFrom(@Nonnull DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else {
            Entity entity = source.getTrueSource();
            if (entity instanceof PlayerEntity && !((PlayerEntity) entity).isCreative() && this.canEntityBeSeen(entity)) {
                this.becomeAngryAt((LivingEntity) entity);
            }
            return super.attackEntityFrom(source, amount);
        }
    }

    @Override
    public void setRevengeTarget(@Nullable LivingEntity livingBase) {
        super.setRevengeTarget(livingBase);
        if (livingBase != null) {
            this.angerTargetUUID = livingBase.getUniqueID();
        }
    }

    private boolean becomeAngryAt(LivingEntity livingEntity) {
        this.angerLevel = this.getAngryAmount();
        this.setRevengeTarget(livingEntity);
        return true;
    }

    private int getAngryAmount() {
        return 250 + this.rand.nextInt(400);
    }

    public boolean isAngry() {
        return this.angerLevel > 0;
    }

    @Override
    public void writeAdditional(@Nonnull CompoundNBT compound) {
        super.writeAdditional(compound);
        if (this.hasSkinVariants()) {
            compound.putInt("Variant", this.getVariant());
        }
        compound.putShort("Anger", (short) this.angerLevel);
        if (this.angerTargetUUID != null) {
            compound.putString("HurtBy", this.angerTargetUUID.toString());
        } else {
            compound.putString("HurtBy", "");
        }
    }

    @Override
    public void readAdditional(@Nonnull CompoundNBT compound) {
        super.readAdditional(compound);
        if (this.hasSkinVariants()) {
            this.setVariant(compound.getInt("Variant"));
        }
        this.angerLevel = compound.getShort("Anger");
        String hurtBy = compound.getString("HurtBy");

        if (!hurtBy.isEmpty()) {
            this.angerTargetUUID = UUID.fromString(hurtBy);
            PlayerEntity player = this.world.getPlayerByUuid(this.angerTargetUUID);
            if (player != null) {
                this.setRevengeTarget(player);
                this.attackingPlayer = player;
                this.recentlyHit = this.getRevengeTimer();
            }
        }
    }

    @Override
    public boolean attackEntityAsMob(@Nonnull Entity entity) { //Copied from MobEntity, to allow Efreet to attack
        float attackDamage = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
        float knockback = (float) this.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
        if (entity instanceof LivingEntity) {
            attackDamage += EnchantmentHelper.getModifierForCreature(this.getHeldItemMainhand(), ((LivingEntity) entity).getCreatureAttribute());
            knockback += (float) EnchantmentHelper.getKnockbackModifier(this);
        }

        int fireAspect = EnchantmentHelper.getFireAspectModifier(this);
        if (fireAspect > 0) {
            entity.setFire(fireAspect * 4);
        }

        boolean attackEntity = entity.attackEntityFrom(DamageSource.causeMobDamage(this), attackDamage);
        if (attackEntity) {
            if (knockback > 0.0F && entity instanceof LivingEntity) {
                ((LivingEntity) entity).applyKnockback(knockback * 0.5F, MathHelper.sin(this.rotationYaw * ((float) Math.PI / 180F)), -MathHelper.cos(this.rotationYaw * ((float) Math.PI / 180F)));
                this.setMotion(this.getMotion().mul(0.6D, 1.0D, 0.6D));
            }
            this.applyEnchantments(this, entity);
        }
        return attackEntity;
    }

    static class AIHurtByAggressor extends HurtByTargetGoal {
        AIHurtByAggressor(EfreetBaseEntity efreet) {
            super(efreet);
            this.setCallsForHelp();
        }

        @Override
        protected void setAttackTarget(@Nonnull MobEntity entity, @Nonnull LivingEntity living) {
            if (entity instanceof EfreetBaseEntity && this.goalOwner.canEntityBeSeen(living) && ((EfreetBaseEntity) entity).becomeAngryAt(living)) {
                entity.setAttackTarget(living);
            }
        }

        @Override
        public boolean shouldContinueExecuting() {
            EfreetBaseEntity efreet = ((EfreetBaseEntity) this.goalOwner);
            return efreet.isAngry() && super.shouldContinueExecuting();
        }
    }

    static class AITargetAggressor extends NearestAttackableTargetGoal<PlayerEntity> {
        AITargetAggressor(EfreetBaseEntity efreet) {
            super(efreet, PlayerEntity.class, true);
        }

        @Override
        public boolean shouldExecute() {
            EfreetBaseEntity efreet = ((EfreetBaseEntity) this.goalOwner);
            return efreet.isAngry() && super.shouldExecute();
        }

        @Override
        public boolean shouldContinueExecuting() {
            EfreetBaseEntity efreet = ((EfreetBaseEntity) this.goalOwner);
            return efreet.isAngry() && super.shouldContinueExecuting();
        }
    }
}