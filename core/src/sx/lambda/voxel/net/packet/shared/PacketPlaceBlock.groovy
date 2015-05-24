package sx.lambda.voxel.net.packet.shared

import groovy.transform.CompileStatic
import io.netty.channel.ChannelHandlerContext
import sx.lambda.voxel.VoxelGameClient
import sx.lambda.voxel.net.packet.SharedPacket
import sx.lambda.voxel.server.VoxelGameServer
import sx.lambda.voxel.server.net.ConnectedClient
import sx.lambda.voxel.server.net.ConnectionStage
import sx.lambda.voxel.util.Vec3i

@CompileStatic
class PacketPlaceBlock implements SharedPacket {

    private final int block
    private final int x, y, z

    public PacketPlaceBlock(int x, int y, int z, int block) {
        this.x = x; this.y = y; this.z = z;
        this.block = block
    }

    @Override
    void handleServerReceive(VoxelGameServer server, ChannelHandlerContext ctx) {
        server.world.addBlock(block, x, y, z)
        for (ConnectedClient client : server.getClientList()) {
            if (client.stage == ConnectionStage.PLAY) {
                client.context.writeAndFlush(this)
            }
        }
    }

    @Override
    void handleClientReceive(ChannelHandlerContext ctx) {
        VoxelGameClient.instance.world.addBlock(block, x, y, z)
    }

}
