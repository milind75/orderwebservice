# GitHub Keyword Scanner

This utility scans all public repositories for a GitHub user and generates a CSV with matched keywords.

## Output format

CSV columns:

- `project_name`
- `keyword`

## Default behavior

- User: `milind75`
- Keywords: `RestController,FastAPI`
- Output: `build/reports/github-keywords.csv`

## Run

```powershell
.\gradlew.bat scanGithubKeywords
```

## Custom run

```powershell
.\gradlew.bat scanGithubKeywords -PscannerArgs="--user milind75 --keywords RestController,FastAPI --output build/reports/my-keywords.csv"
```

## Optional GitHub token

Set `GITHUB_TOKEN` to increase API rate limits.

```powershell
$env:GITHUB_TOKEN = "<your-token>"
.\gradlew.bat scanGithubKeywords
```

