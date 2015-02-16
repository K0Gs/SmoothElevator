package org.zzl.minegaming.SmoothElevator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import net.minecraft.server.v1_7_R4.*;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R4.event.CraftEventFactory;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Vector;

public class NewFloatingBlock extends EntityFallingBlock
{
	boolean ignoreGravity = true;
	
	public Block id;
	public int data;
	public int ticksLived;
	public boolean dropItem;
	private boolean f;
	private boolean hurtEntities;
	private int fallHurtMax;
	private float fallHurtAmount;
	public NBTTagCompound tileEntityData;

	public NewFloatingBlock(World world)
	{
		super(world);
		this.dropItem = true;
		this.fallHurtMax = 40;
		this.fallHurtAmount = 2.0F;
	}

	public NewFloatingBlock(World world, double d0, double d1, double d2, Block block)
	{
		this(world, d0, d1, d2, block, 0);
	}

	public NewFloatingBlock(World world, double d0, double d1, double d2, Block block, int i)
	{
		super(world);
		this.dropItem = true;
		this.fallHurtMax = 40;
		this.fallHurtAmount = 2.0F;
		this.id = block;
		this.data = i;
		this.k = true;
		a(0.98F, 0.98F);
		this.height = (this.length / 2.0F);
		setPosition(d0, d1, d2);
		this.motX = 0.0D;
		this.motY = 0.0D;
		this.motZ = 0.0D;
		this.lastX = d0;
		this.lastY = d1;
		this.lastZ = d2;
	}

	protected boolean g_()
	{
		return false;
	}

	protected void c()
	{
	}

	public boolean R()
	{
		return !this.dead;
	}

	public void h()
	{
		if (this.id.getMaterial() == Material.AIR)
		{
			die();
		}
		else
		{
			this.lastX = this.locX;
			this.lastY = this.locY;
			this.lastZ = this.locZ;
			this.ticksLived += 1;
			if (!this.ignoreGravity)
			{
				this.motY -= 0.03999999910593033D;
			}
			move(this.motX, this.motY, this.motZ);
			this.motX *= 0.9800000190734863D;
			if (!this.ignoreGravity)
			{
				this.motY *= 0.9800000190734863D;
			}
			this.motZ *= 0.9800000190734863D;
			if (!this.world.isStatic)
			{
				int i = MathHelper.floor(this.locX);
				int j = MathHelper.floor(this.locY);
				int k = MathHelper.floor(this.locZ);

				if (this.ticksLived == 1)
				{
					if ((this.ticksLived != 1) || (this.world.getType(i, j, k) != this.id) || (this.world.getData(i, j, k) != this.data) || (CraftEventFactory.callEntityChangeBlockEvent(this, i, j, k, Blocks.AIR, 0).isCancelled()))
					{
						if (!this.ignoreGravity) {
							die();
						}
						return;
					}
					if (!this.ignoreGravity)
						this.world.setAir(i, j, k);
				}

				if (this.onGround)
				{
					this.motX *= 0.699999988079071D;
					this.motZ *= 0.699999988079071D;
					if (!this.ignoreGravity)
					{
						this.motY *= -0.5D;
					}
					if (this.world.getType(i, j, k) != Blocks.PISTON_MOVING && (!this.ignoreGravity))
					{
						die();

						if ((!this.f) && (this.world.mayPlace(this.id, i, j, k, true, 1, (Entity) null, (ItemStack) null)) && (!BlockFalling.canFall(this.world, i, j - 1, k)) && (i >= -30000000) && (k >= -30000000) && (i < 30000000) && (k < 30000000) && (j > 0) && (j < 256) && ((this.world.getType(i, j, k) != this.id) || (this.world.getData(i, j, k) != this.data)))
						{
							if (CraftEventFactory.callEntityChangeBlockEvent(this, i, j, k, this.id, this.data).isCancelled())
							{
								return;
							}
							this.world.setTypeAndData(i, j, k, this.id, this.data, 3);

							if ((this.id instanceof BlockFalling))
							{
								((BlockFalling) this.id).a(this.world, i, j, k, this.data);
							}

							if ((this.tileEntityData != null) && ((this.id instanceof IContainer)))
							{
								TileEntity tileentity = this.world.getTileEntity(i, j, k);

								if (tileentity != null)
								{
									NBTTagCompound nbttagcompound = new NBTTagCompound();

									tileentity.b(nbttagcompound);
									Iterator iterator = this.tileEntityData.c().iterator();

									while (iterator.hasNext())
									{
										String s = (String) iterator.next();
										NBTBase nbtbase = this.tileEntityData.get(s);

										if ((!s.equals("x")) && (!s.equals("y")) && (!s.equals("z")))
										{
											nbttagcompound.set(s, nbtbase.clone());
										}
									}

									tileentity.a(nbttagcompound);
									tileentity.update();
								}
							}
						}
						else if ((this.dropItem) && (!this.f))
						{
							a(new ItemStack(this.id, 1, this.id.getDropData(this.data)), 0.0F);
						}
					}
				}
				else if (((this.ticksLived > 100) && (!this.world.isStatic) && ((j < 1) || (j > 256))) || ((this.ticksLived > 600) && 
						(!this.ignoreGravity)))
				{
					if (this.dropItem)
					{
						a(new ItemStack(this.id, 1, this.id.getDropData(this.data)), 0.0F);
					}

					die();
				}
			}
		}
	}

	protected void b(float f)
	{
		if (this.hurtEntities)
		{
			int i = MathHelper.f(f - 1.0F);

			if (i > 0)
			{
				ArrayList arraylist = new ArrayList(this.world.getEntities(this, this.boundingBox));
				boolean flag = this.id == Blocks.ANVIL;
				DamageSource damagesource = flag ? DamageSource.ANVIL : DamageSource.FALLING_BLOCK;
				Iterator iterator = arraylist.iterator();

				while (iterator.hasNext())
				{
					Entity entity = (Entity) iterator.next();

					CraftEventFactory.entityDamage = this;
					entity.damageEntity(damagesource, Math.min(MathHelper.d(i * this.fallHurtAmount), this.fallHurtMax));
					CraftEventFactory.entityDamage = null;
				}

				if ((flag) && (this.random.nextFloat() < 0.0500000007450581D + i * 0.05D))
				{
					int j = this.data >> 2;
					int k = this.data & 0x3;

					j++;
					if (j > 2)
						this.f = true;
					else
						this.data = (k | j << 2);
				}
			}
		}
	}

	protected void b(NBTTagCompound nbttagcompound)
	{
		nbttagcompound.setByte("Tile", (byte) Block.getId(this.id));
		nbttagcompound.setInt("TileID", Block.getId(this.id));
		nbttagcompound.setByte("Data", (byte) this.data);
		nbttagcompound.setByte("Time", (byte) this.ticksLived);
		nbttagcompound.setBoolean("DropItem", this.dropItem);
		nbttagcompound.setBoolean("HurtEntities", this.hurtEntities);
		nbttagcompound.setFloat("FallHurtAmount", this.fallHurtAmount);
		nbttagcompound.setInt("FallHurtMax", this.fallHurtMax);
		if (this.tileEntityData != null)
			nbttagcompound.set("TileEntityData", this.tileEntityData);
	}

	protected void a(NBTTagCompound nbttagcompound)
	{
		if (nbttagcompound.hasKeyOfType("TileID", 99))
			this.id = Block.getById(nbttagcompound.getInt("TileID"));
		else
		{
			this.id = Block.getById(nbttagcompound.getByte("Tile") & 0xFF);
		}

		this.data = (nbttagcompound.getByte("Data") & 0xFF);
		this.ticksLived = (nbttagcompound.getByte("Time") & 0xFF);
		if (nbttagcompound.hasKeyOfType("HurtEntities", 99))
		{
			this.hurtEntities = nbttagcompound.getBoolean("HurtEntities");
			this.fallHurtAmount = nbttagcompound.getFloat("FallHurtAmount");
			this.fallHurtMax = nbttagcompound.getInt("FallHurtMax");
		}
		else if (this.id == Blocks.ANVIL)
		{
			this.hurtEntities = true;
		}

		if (nbttagcompound.hasKeyOfType("DropItem", 99))
		{
			this.dropItem = nbttagcompound.getBoolean("DropItem");
		}

		if (nbttagcompound.hasKeyOfType("TileEntityData", 10))
		{
			this.tileEntityData = nbttagcompound.getCompound("TileEntityData");
		}

		if (this.id.getMaterial() == Material.AIR)
			this.id = Blocks.SAND;
	}

	public void a(boolean flag)
	{
		this.hurtEntities = flag;
	}

	public void a(CrashReportSystemDetails crashreportsystemdetails)
	{
		super.a(crashreportsystemdetails);
		crashreportsystemdetails.a("Immitating block ID", Integer.valueOf(Block.getId(this.id)));
		crashreportsystemdetails.a("Immitating block data", Integer.valueOf(this.data));
	}

	public Block f()
	{
		return this.id;
	}

	public Vector getVelocity()
	{
		return new Vector(this.motX, this.motY, this.motZ);
	}

	public void setVelocity(Vector vel)
	{
		this.motX = vel.getX();
		this.motY = vel.getY();
		this.motZ = vel.getZ();
		this.velocityChanged = true;
	}
	
	public Location getLocation()
	{
		return new Location(getWorld(), this.locX, this.locY, this.locZ);
	}

	public void setLocation(Location l)
	{
		this.locX = l.getX();
		this.locY = l.getY();
		this.locZ = l.getZ();
	}
	
	public CraftWorld getWorld() {
		return ((WorldServer)this.world).getWorld();
	}
	
	public org.bukkit.Material getMaterial()
	{
		return org.bukkit.Material.getMaterial(Block.getId(this.id));
		
	}

	public int getBlockId() {
		return Block.getId(this.id);
	}

	public byte getBlockData()
	{
		return (byte)this.data;
	}
	
	public void remove()
	{
		die();
	}
	
	public boolean teleport(Location location) {
		return teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
	}

	public boolean teleport(Location location, PlayerTeleportEvent.TeleportCause cause) {
		this.world = ((CraftWorld)location.getWorld()).getHandle();
		setLocation(location.getX(), location.getY(), location.getZ(), 
				location.getYaw(), location.getPitch());

		return true;
	}
}
