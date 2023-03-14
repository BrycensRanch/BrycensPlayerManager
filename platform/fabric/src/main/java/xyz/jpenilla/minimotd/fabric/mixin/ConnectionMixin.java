
package xyz.jpenilla.minimotd.fabric.mixin;

import net.minecraft.network.Connection;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import xyz.jpenilla.minimotd.fabric.access.ConnectionAccess;

@Unique
@Mixin(Connection.class)
@Implements({@Interface(iface = ConnectionAccess.class, prefix = "brycensPlayerManager$")})
abstract class ConnectionMixin {
  private int brycensPlayerManager$protocolVersion = -1;

  public void brycensPlayerManager$protocolVersion(final int protocolVersion) {
    this.brycensPlayerManager$protocolVersion = protocolVersion;
  }

  public int brycensPlayerManager$protocolVersion() {
    return this.brycensPlayerManager$protocolVersion;
  }
}
