package client.gui.resources;
/*
A centralized way of keeping all non-java/css/fxml files used by the program. This allows me to have one path to this
folder at runtime and do path + "power.png" for example. Therefore it improves efficiency and lessens code duplicates
by removing the need call this::getClass().getResource("path/path/path/image.png") every time I want to load a resource.
 */