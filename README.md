API Test Automation — README

Overview
- This project runs API tests with TestNG and generates Allure reports.
- ZAP active-scan is available and will run only when `zapEnabled` is true (configured in `testng.xml`).


## 📊 Reports

### ✅ Allure Test Report
👉 [Open Allure Report](https://chethan-s-g.github.io/API-Security-Testing/)

### 🔐 ZAP Security Report
👉 [Open ZAP Report](https://chethan-s-g.github.io/API-Security-Testing/zap/index.html)

```

Prerequisites
- Java 11+ and Maven installed and on PATH.
- Node/npm (optional) for running Allure via `npx`, or Allure CLI installed globally.

Quick run (generate Allure results)
1. Run tests with Maven:

```powershell
mvn test
```

2. After the run, Allure results are created in the project's `allure-results` folder (or under `target` if configured).

Serve Allure report (recommended: use npx to avoid PowerShell execution policy issues)

```powershell
# Using npx (no install required)
npx allure-commandline@2.42.0 serve allure-results

# If you have global allure installed and need to bypass PowerShell policy (one-off):
# powershell -NoProfile -ExecutionPolicy Bypass -Command "allure serve target/allure-results"
```

Enable ZAP active-scan
- By default `testng.xml` sets `zapEnabled` to `false` so only API tests run.
- To enable ZAP active-scan:
  1. Start ZAP locally and ensure it listens on `localhost:8081`.
  2. Edit `testng.xml` and set the suite parameter `<parameter name="zapEnabled" value="true"/>`.
  3. Run `mvn test` — `BaseTest` will initialize the ZAP client in `@BeforeSuite` and run the active scan in `@AfterSuite`. The HTML report will be written to `zap-report.html` in the project root.

Notes about Suite name in Allure
- The TestNG suite name comes from `testng.xml` (`<suite name="API Test Automation">`). The Maven Surefire plugin is configured to use `testng.xml` so the suite name appears correctly in reports.

Removing BaseTest (configuration) entries from Allure
- Allure may show TestNG configuration methods (like `@BeforeSuite`) under configuration/test lifecycle. These are not test cases.
- Option A: Filter out configuration entries in the Allure UI (use UI filters).
- Option B: Remove configuration result files before serving the report (one-off). Example PowerShell (run from project root):

```powershell
# WARNING: deletes files that match the patterns; backup if needed.
Get-ChildItem allure-results\*-result.json | Where-Object { (Get-Content $_.FullName) -match 'BaseTest|beforeSuite|afterSuite' } | Remove-Item
```

Troubleshooting
- If `allure serve` fails in PowerShell with an execution policy error, use the `npx` command shown above or the one-off `-ExecutionPolicy Bypass` wrapper.
- If no `allure-results` folder is present after `mvn test`, check `target/surefire-reports` to ensure tests actually ran and that `allure-testng` is on the classpath.

If you want, I can:
- Run `mvn test` now and serve the report for you (I can use `npx` to serve it).

