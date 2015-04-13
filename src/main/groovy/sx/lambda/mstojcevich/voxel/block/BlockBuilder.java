package sx.lambda.mstojcevich.voxel.block;

import java.net.URL;

public class BlockBuilder {

    private String humanName = "Undefined";
    private URL textureLocation = BlockBuilder.class.getResource("/textures/block/undefined.png");
    private int id = -1;
    private boolean transparent = false;
    private IBlockRenderer renderer;
    private boolean solid = true;

    public BlockBuilder setHumanName(String hn) {
        this.humanName = hn;
        return this;
    }

    public BlockBuilder setTextureLocation(URL tl) {
        this.textureLocation = tl;
        return this;
    }

    public BlockBuilder setID(int id) {
        this.id = id;
        return this;
    }

    public BlockBuilder setTransparent(boolean transparent) {
        this.transparent = transparent;
        return this;
    }

    public BlockBuilder setSolid(boolean solid) {
        this.solid = solid;
        return this;
    }

    public BlockBuilder setBlockRenderer(IBlockRenderer renderer) {
        this.renderer = renderer;
        return this;
    }

    public Block build() throws MissingElementException {
        if(id == -1) throw new MissingElementException("id");
        if(renderer == null) {
            renderer = new NormalBlockRenderer(id);
        }
        return new Block(id, humanName, renderer, textureLocation, transparent, solid);
    }

    public class MissingElementException extends Exception {
        public MissingElementException(String missingEl) {
            super("You cannot create a block without " + missingEl);
        }
    }

}
