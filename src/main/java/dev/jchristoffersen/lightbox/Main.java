public class Main {
    public static void main(String[] args) {
        sceneManager = new SceneManager(scenes, transitions, 60);
        ledMatrix = new RaspberryPiLedMatrix();
        renderLoop = new RenderLoop(sceneManager, ledMatrix);
        renderLoop.start();
    }
}
