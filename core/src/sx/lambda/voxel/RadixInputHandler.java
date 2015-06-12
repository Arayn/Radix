package sx.lambda.voxel;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import sx.lambda.voxel.client.gui.screens.BlockSelectGUI;
import sx.lambda.voxel.client.keybind.Keybind;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RadixInputHandler implements InputProcessor {

    private static final float mouseSensitivity = 0.03f;
    private final RadixClient game;
    private final List<Keybind> keybindList = Collections.synchronizedList(new ArrayList<Keybind>());
    private int lastMouseX = -Integer.MAX_VALUE;
    private int lastMouseY = -Integer.MAX_VALUE;

    public RadixInputHandler(final RadixClient game) {
        this.game = game;

        // Register the default keybinds
        registerKeybind(new Keybind("voxeltest.movement.jump", "Jump", Input.Keys.SPACE, () -> {
            if (game.getWorld() != null && game.getCurrentScreen().equals(game.getHud())) {
                game.getMovementHandler().jump();
            }

        }));
        registerKeybind(new Keybind("voxeltest.gui.selectblock", "Select Block GUI", Input.Keys.E, () -> {
            if (game.getWorld() != null && game.getCurrentScreen().equals(game.getHud())) {
                game.addToGLQueue(() -> game.setCurrentScreen(new BlockSelectGUI(game.getHud().getIcons())));
            }

        }));
        registerKeybind(new Keybind("voxeltest.gui.back", "Back", Input.Keys.ESCAPE, () -> {
            if (game.getWorld() != null) {
                if (!game.getCurrentScreen().equals(game.getHud())) {
                    game.addToGLQueue(() -> game.setCurrentScreen(game.getHud()));
                } else {
                    game.exitWorld();// TODO show ingame options
                }
            }
        }));
        registerKeybind(new Keybind("voxeltest.gui.chat", "Open Chat", Input.Keys.T, () -> {
            if (game.getWorld() != null) {
                if (game.getCurrentScreen().equals(game.getHud())) {
                    game.addToGLQueue(new Runnable() {
                        @Override
                        public void run() {
                            game.setCurrentScreen(game.getChatGUI());
                        }

                    });
                }
            }
        }));
        registerKeybind(new Keybind("voxeltest.debug.wire", "Enable Wireframe", Input.Keys.X, new Runnable() {
            @Override
            public void run() {
                if (game.getWorld() != null) {
                    if (game.getCurrentScreen().equals(game.getHud())) {
                        game.addToGLQueue(() -> game.setWireframe(!game.isWireframe()));
                    }
                }
            }
        }));
    }

    @Override
    public boolean keyDown(int keycode) {
        for (Keybind kb : keybindList) {
            if (kb.getKey() == keycode) {
                kb.press();
            }
        }

        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        RadixClient.getInstance().getCurrentScreen().keyTyped(character);
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        game.getCurrentScreen().onMouseClick(button);
        switch (button) {
            case 0:
//                if (game.getWorld() != null) {
//                    game.breakBlock();
//                }
                break;
            case 1:
                if (game.getWorld() != null) {
                    game.placeBlock();
                }
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (!game.onAndroid())
            updateRotation(screenX, screenY);

        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        if (!game.onAndroid())
            updateRotation(screenX, screenY);

        return true;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    public void registerKeybind(Keybind kb) {
        this.keybindList.add(kb);
    }

    private void updateRotation(int newMouseX, int newMouseY) {
        if (lastMouseX > -Integer.MAX_VALUE) {
            if ((game.getWorld() != null || game.getPlayer() != null) && (game.getCurrentScreen() == null || game.getCurrentScreen().equals(game.getHud()))) {
                int deltaX = newMouseX - lastMouseX;
                int deltaY = newMouseY - lastMouseY;
                float deltaYaw = deltaX * mouseSensitivity;
                float deltaPitch = -deltaY * mouseSensitivity;

                float newPitch = Math.abs(game.getPlayer().getRotation().getPitch() + deltaPitch);
                if (newPitch > 90) {
                    deltaPitch = 0;
                }

                game.getPlayer().getRotation().offset(deltaPitch, deltaYaw);

                if (Math.abs(deltaPitch) > 0 || Math.abs(deltaYaw) > 0) {
                    game.getPlayer().getRotation().offset(deltaPitch, deltaYaw);
                    game.updateSelectedBlock();
                    game.getGameRenderer().calculateFrustum();
                }
            }
        }

        lastMouseX = newMouseX;
        lastMouseY = newMouseY;
    }

}
