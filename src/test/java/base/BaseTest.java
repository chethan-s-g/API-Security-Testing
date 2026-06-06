package base;

import io.restassured.RestAssured;
import io.qameta.allure.restassured.AllureRestAssured;
import io.qameta.allure.Attachment;

import org.testng.annotations.Parameters;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Optional;
import org.testng.annotations.AfterSuite;

import org.zaproxy.clientapi.core.ClientApi;
import org.zaproxy.clientapi.core.Alert;
import org.zaproxy.clientapi.core.ApiResponse;
import org.zaproxy.clientapi.core.ApiResponseElement;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class BaseTest {

    public static boolean zapEnabled = false;
    private static ClientApi zapClient;

    private static final String API_BASE = "http://localhost:8081/api";
    private static final String TARGET = "http://localhost:8081";

    // ✅ BEFORE SUITE
    @BeforeSuite
    @Parameters({"zapEnabled"})
    public void beforeSuite(@Optional("false") String zapFlag) {

        zapEnabled = Boolean.parseBoolean(zapFlag);

        RestAssured.baseURI = API_BASE;

        if (zapEnabled) {
            // ✅ ZAP proxy (correct port)
            RestAssured.proxy("localhost", 8080);
            zapClient = new ClientApi("localhost", 8080);

            System.out.println("✅ ZAP ENABLED");
        } else {
            System.out.println("✅ ZAP DISABLED");
        }

        // ✅ Allure request/response logging
        RestAssured.filters(new AllureRestAssured());
    }

    // ✅ AFTER SUITE → ZAP SCAN + SUMMARY + REPORT
    @AfterSuite
    public void runZapAfterSuite() throws Exception {

        if (!zapEnabled) {
            System.out.println("✅ Skipping ZAP Scan");
            return;
        }

        System.out.println("🚀 Starting ZAP Scan...");

        ApiResponse scanResp = zapClient.ascan.scan(
                TARGET,
                "True",
                "False",
                null, null, null
        );

        String scanId = ((ApiResponseElement) scanResp).getValue();

        int progress;
        do {
            Thread.sleep(5000);
            progress = Integer.parseInt(
                    ((ApiResponseElement) zapClient.ascan.status(scanId)).getValue()
            );
            System.out.println("ZAP Progress: " + progress + "%");
        } while (progress < 100);

        System.out.println("✅ ZAP Scan Completed");

        // ✅ FETCH ALERTS
        List<Alert> alerts = zapClient.getAlerts(null, 0, 100);

        int high = 0;
        int medium = 0;
        int low = 0;

        // ✅ FINAL FIX (ENUM BASED — NO ERROR)
        for (Alert alert : alerts) {

            Alert.Risk risk = alert.getRisk();

            if (risk == Alert.Risk.High) {
                high++;
            } else if (risk == Alert.Risk.Medium) {
                medium++;
            } else if (risk == Alert.Risk.Low) {
                low++;
            }
        }

        // ✅ ✅ ✅ YOUR REQUIRED SUMMARY (CORRECT PLACE)
        String summary =
                "\n=== ZAP SUMMARY ===\n" +
                "High   : " + high + "\n" +
                "Medium : " + medium + "\n" +
                "Low    : " + low + "\n\n" +
                "👉 Open Full Report: ../zap/index.html";

        System.out.println(summary);

        // ✅ Attach summary to Allure
        attachSummary(summary);

        // ✅ Generate HTML report
        byte[] report = zapClient.core.htmlreport();
        Files.write(Paths.get("zap-report.html"), report);

        // ✅ Attach full report to Allure
        attachHtmlReport(new String(report));

        System.out.println("✅ ZAP Summary + Report attached");
    }

    // ✅ Allure attachment - summary
    @Attachment(value = "ZAP Summary", type = "text/plain")
    public String attachSummary(String summary) {
        return summary;
    }

    // ✅ Allure attachment - HTML report
    @Attachment(value = "ZAP Full Report", type = "text/html")
    public String attachHtmlReport(String html) {
        return html;
    }
}