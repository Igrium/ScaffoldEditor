package com.igrium.scaffold.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.igrium.scaffold.asset.AssetManager;
import com.igrium.scaffold.util.MappedListView;

public class Project {
    public static final String SETTINGS_FILE = "project.json";

    private final Path projectFolder;
    private AssetManager assetManager;

    private ProjectSettings projectSettings = new ProjectSettings();

    public Project(Path projectFolder) {
        this.projectFolder = projectFolder;
    }

    public Path getProjectFolder() {
        return projectFolder;
    }

    // SETTINGS

    public ProjectSettings getProjectSettings() {
        return projectSettings;
    }

    public void setProjectSettings(ProjectSettings projectSettings) {
        this.projectSettings = projectSettings;
    }

    public List<Path> getSearchPaths() {
        return new MappedListView<>(() -> projectSettings.getSearchPaths(), this::makePathGlobal);
    }

    public void readProjectSettings() throws IOException {
        Path settingsFile = projectFolder.resolve(SETTINGS_FILE);
        if (!Files.isRegularFile(settingsFile)) return; // There are no settings to read.

        String json = Files.readString(settingsFile);
        projectSettings = ProjectSettings.fromJson(json);
    }

    public void saveProjectSettings() throws IOException {
        String json = ProjectSettings.toJson(projectSettings);
        Path settingsFile = projectFolder.resolve(SETTINGS_FILE);
        Files.writeString(settingsFile, json);
    }

    // PATHS

    /**
     * Turn a path into a global path relative to this project folder.
     */
    private Path makePathGlobal(Path path) {
        if (path.isAbsolute()) return path;
        return projectFolder.resolve(path);
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    public void initAssetManager() {
        if (assetManager != null) throw new IllegalStateException("Asset manager is already initialized!");
        assetManager = new AssetManager();
        assetManager.setSearchDirectories(getSearchPaths());
    }

}
