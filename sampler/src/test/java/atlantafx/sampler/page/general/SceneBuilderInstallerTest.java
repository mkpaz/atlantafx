/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.general;

import atlantafx.base.theme.PrimerDark;
import atlantafx.base.theme.PrimerLight;
import atlantafx.sampler.theme.SamplerTheme;
import atlantafx.sampler.theme.SceneBuilderTheme;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class SceneBuilderInstallerTest {

    private static final Path INSTALL_DIR = Paths.get("/opt/scenebuilder");

    //@Test
    public void testInstall() {
        var installer = new SceneBuilderInstaller(INSTALL_DIR);
        installer.install(Map.of(
            SceneBuilderTheme.CASPIAN_THEMES.get(0), new SamplerTheme(new PrimerLight()),
            SceneBuilderTheme.CASPIAN_THEMES.get(1), new SamplerTheme(new PrimerDark())
        ));
    }

    //@Test
    public void testUninstall() {
        var installer = new SceneBuilderInstaller(INSTALL_DIR);
        installer.uninstall();
    }
}