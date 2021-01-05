package client.data;

public class BackgroundWorker {

    /*
    Because I don't want to change source code of JavaFX, I can't just set the instance of customColors in ColorPicker
    to the same pointer by using the keyword static. That's why I use a background worker in a different thread to do
    some computing and merge the lists together. This should be improved later!
     */
    public static void colorPickerSyncColors() {

    }

}
