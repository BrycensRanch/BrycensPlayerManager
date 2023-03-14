
package xyz.jpenilla.minimotd.fabric.access;

public interface ConnectionAccess {
  void protocolVersion(int protocolVersion);

  int protocolVersion();
}
