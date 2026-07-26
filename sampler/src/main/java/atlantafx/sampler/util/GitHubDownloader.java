/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.util;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for fetching the latest release from GitHub repositories.
 */
public class GitHubDownloader {

    private static final String GITHUB_API_URL = "https://api.github.com/repos/%s/%s/releases/latest";
    private static final String DOWNLOAD_LINK_FORMAT = "https://github.com/%s/%s/archive/refs/tags/%s.zip";
    private static final Pattern TAG_NAME_PATTERN = Pattern.compile("\"tag_name\"\\s*:\\s*\"([^\"]+)\"");

    private final HttpClient httpClient;

    public GitHubDownloader() {
        this.httpClient = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();
    }

    /**
     * Downloads and extracts the latest release archive for a given GitHub repository.
     *
     * @param owner     the repository owner
     * @param repo      the repository name
     * @param targetDir directory where the archive should be extracted
     * @return Path to the root directory of the extracted content
     */
    public Path downloadLatestRelease(String owner, String repo, Path targetDir)
        throws IOException, InterruptedException {

        if (Files.notExists(targetDir)) {
            Files.createDirectories(targetDir);
        }

        String latestTag = fetchLatestTagName(owner, repo);
        String downloadUrl = String.format(DOWNLOAD_LINK_FORMAT, owner, repo, latestTag);
        Path zipFile = targetDir.resolve(String.format("%s-%s.zip", repo, latestTag));

        try {
            if (Files.notExists(zipFile)) {
                downloadFile(downloadUrl, zipFile);
            }

            return IOUtils.extractZipArchive(zipFile, targetDir);
        } finally {
            Files.deleteIfExists(zipFile);
        }
    }

    /**
     * Queries the GitHub REST API to obtain the tag name of the latest release.
     *
     * @param owner the repository owner
     * @param repo  the repository name
     * @return the latest tag name (e.g. "v1.0.0")
     */
    public String fetchLatestTagName(String owner, String repo)
        throws IOException, InterruptedException {

        String apiUrl = String.format(GITHUB_API_URL, owner, repo);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(apiUrl))
            .header("Accept", "application/vnd.github+json")
            .header("User-Agent", "mkpaz/atlantafx")
            .GET()
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException(
                "Failed to fetch release info from GitHub API. HTTP status: %d, body: '%s'.".formatted(
                    response.statusCode(), response.body()
                )
            );
        }

        Matcher matcher = TAG_NAME_PATTERN.matcher(response.body());
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            throw new IOException("Unable to parse 'tag_name' from GitHub API response.");
        }
    }

    private void downloadFile(String downloadUrl, Path destinationPath) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(downloadUrl))
            .header("User-Agent", "mkpaz/atlantafx")
            .GET()
            .build();

        HttpResponse<Path> response = httpClient.send(request, HttpResponse.BodyHandlers.ofFile(destinationPath));

        if (response.statusCode() != 200) {
            // remove incomplete or corrupted file if server returned an error
            Files.deleteIfExists(destinationPath);

            throw new IOException(
                "Failed to download release from '%s'. HTTP status: %d".formatted(downloadUrl, response.statusCode())
            );
        }
    }
}