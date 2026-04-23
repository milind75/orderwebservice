package com.orderservice.tools;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.client.RestClient;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class GitHubKeywordScanner {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final Set<String> SCANNABLE_EXTENSIONS = Set.of(
            ".java", ".kt", ".kts", ".groovy", ".py", ".js", ".ts", ".tsx",
            ".jsx", ".go", ".rb", ".php", ".cs", ".cpp", ".c", ".h", ".hpp",
            ".xml", ".yml", ".yaml", ".json", ".md", ".txt", ".sql", ".sh"
    );

    public static void main(String[] args) throws Exception {
        ScannerOptions options = ScannerOptions.fromArgs(args);
        GitHubKeywordScanner scanner = new GitHubKeywordScanner(options.token());

        List<CsvRow> rows = scanner.scanUserRepositories(options.user(), options.keywords());
        writeCsv(options.outputPath(), rows);

        System.out.println("Wrote " + rows.size() + " row(s) to " + options.outputPath().toAbsolutePath());
    }

    private final RestClient restClient;

    public GitHubKeywordScanner(String githubToken) {
        this.restClient = RestClient.builder()
                .defaultHeader("Accept", "application/vnd.github+json")
                .defaultHeader("User-Agent", "orderwebservice-keyword-scanner")
                .defaultHeader("X-GitHub-Api-Version", "2022-11-28")
                .defaultHeaders(headers -> {
                    if (githubToken != null && !githubToken.isBlank()) {
                        headers.setBearerAuth(githubToken);
                    }
                })
                .build();
    }

    public List<CsvRow> scanUserRepositories(String user, List<String> keywords) throws IOException {
        List<RepoInfo> repos = listRepos(user);
        List<CsvRow> rows = new ArrayList<>();

        for (RepoInfo repo : repos) {
            Set<String> matched = scanRepo(user, repo, keywords);
            for (String keyword : matched.stream().sorted().toList()) {
                rows.add(new CsvRow(repo.name(), keyword));
            }
        }

        rows.sort(Comparator.comparing(CsvRow::projectName).thenComparing(CsvRow::keyword));
        return rows;
    }

    Set<String> scanRepo(String owner, RepoInfo repo, List<String> keywords) throws IOException {
        Set<String> matched = new LinkedHashSet<>();
        JsonNode tree = getJson("https://api.github.com/repos/" + owner + "/" + repo.name() + "/git/trees/" + repo.defaultBranch() + "?recursive=1");

        if (!tree.has("tree") || !tree.get("tree").isArray()) {
            return matched;
        }

        for (JsonNode node : tree.get("tree")) {
            if (!"blob".equals(node.path("type").asText())) {
                continue;
            }

            String path = node.path("path").asText();
            if (!isScannablePath(path)) {
                continue;
            }

            long size = node.path("size").asLong(0);
            if (size > 300_000) {
                continue;
            }

            String sha = node.path("sha").asText();
            String content = getBlobContent(owner, repo.name(), sha);
            Set<String> inFile = findMatchedKeywords(content, keywords);
            matched.addAll(inFile);

            if (matched.size() == new HashSet<>(keywords).size()) {
                break;
            }
        }

        return matched;
    }

    List<RepoInfo> listRepos(String user) throws IOException {
        List<RepoInfo> repos = new ArrayList<>();
        int page = 1;

        while (true) {
            JsonNode root = getJson("https://api.github.com/users/" + user + "/repos?per_page=100&page=" + page);
            if (!root.isArray() || root.isEmpty()) {
                break;
            }

            for (JsonNode repo : root) {
                String name = repo.path("name").asText();
                String defaultBranch = repo.path("default_branch").asText("main");
                if (!name.isBlank()) {
                    repos.add(new RepoInfo(name, defaultBranch));
                }
            }

            if (root.size() < 100) {
                break;
            }
            page++;
        }

        return repos;
    }

    static Set<String> findMatchedKeywords(String content, List<String> keywords) {
        Set<String> matched = new LinkedHashSet<>();
        String lowerContent = content.toLowerCase(Locale.ROOT);
        for (String keyword : keywords) {
            if (lowerContent.contains(keyword.toLowerCase(Locale.ROOT))) {
                matched.add(keyword);
            }
        }
        return matched;
    }

    static boolean isScannablePath(String path) {
        String lower = path.toLowerCase(Locale.ROOT);
        for (String extension : SCANNABLE_EXTENSIONS) {
            if (lower.endsWith(extension)) {
                return true;
            }
        }
        return false;
    }

    static void writeCsv(Path outputPath, List<CsvRow> rows) throws IOException {
        if (outputPath.getParent() != null) {
            Files.createDirectories(outputPath.getParent());
        }

        try (BufferedWriter writer = Files.newBufferedWriter(outputPath, StandardCharsets.UTF_8)) {
            writer.write("project_name,keyword");
            writer.newLine();
            for (CsvRow row : rows) {
                writer.write(csv(row.projectName()));
                writer.write(',');
                writer.write(csv(row.keyword()));
                writer.newLine();
            }
        }
    }

    private static String csv(String value) {
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return '"' + value.replace("\"", "\"\"") + '"';
        }
        return value;
    }

    private JsonNode getJson(String url) throws IOException {
        String body = restClient.get()
                .uri(url)
                .retrieve()
                .body(String.class);
        return OBJECT_MAPPER.readTree(body);
    }

    private String getBlobContent(String owner, String repo, String sha) throws IOException {
        JsonNode blob = getJson("https://api.github.com/repos/" + owner + "/" + repo + "/git/blobs/" + sha);
        if (!"base64".equals(blob.path("encoding").asText())) {
            return "";
        }

        String encoded = blob.path("content").asText("").replace("\n", "");
        if (encoded.isBlank()) {
            return "";
        }

        byte[] decoded = Base64.getDecoder().decode(encoded);
        return new String(decoded, StandardCharsets.UTF_8);
    }

    public record RepoInfo(String name, String defaultBranch) {
    }

    public record CsvRow(String projectName, String keyword) {
    }

    public record ScannerOptions(String user, List<String> keywords, Path outputPath, String token) {
        static ScannerOptions fromArgs(String[] args) {
            String user = "milind75";
            String output = "build/reports/github-keywords.csv";
            List<String> keywords = List.of("RestController", "FastAPI");

            for (int i = 0; i < args.length; i++) {
                switch (args[i]) {
                    case "--user" -> user = nextValue(args, ++i, "--user");
                    case "--keywords" -> keywords = parseKeywords(nextValue(args, ++i, "--keywords"));
                    case "--output" -> output = nextValue(args, ++i, "--output");
                    default -> {
                    }
                }
            }

            String token = System.getenv("GITHUB_TOKEN");
            return new ScannerOptions(user, keywords, Path.of(output), token);
        }

        static List<String> parseKeywords(String raw) {
            String[] parts = raw.split(",");
            List<String> out = new ArrayList<>();
            for (String part : parts) {
                String trimmed = part.trim();
                if (!trimmed.isBlank()) {
                    out.add(trimmed);
                }
            }
            if (out.isEmpty()) {
                throw new IllegalArgumentException("At least one keyword is required");
            }
            return out;
        }

        private static String nextValue(String[] args, int index, String flag) {
            if (index >= args.length) {
                throw new IllegalArgumentException("Missing value for " + flag);
            }
            return args[index];
        }
    }
}

