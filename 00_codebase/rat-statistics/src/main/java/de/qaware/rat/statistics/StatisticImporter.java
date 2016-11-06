package de.qaware.rat.statistics;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import de.qaware.rat.api.models.RatAnomalyModel;

/**
 * The {@code StatisticImporter} class provides functions to import data from
 * the created HTML report.
 * 
 * @author Matthias
 *
 */
public final class StatisticImporter {
    private StatisticImporter() {
    }

    /**
     * Get the current applied anomalies from the HTML report.
     * 
     * @param html
     *            the HTML string of the report.
     * @return the current applied anomalies.
     * @throws IOException
     *             if an I/O error occurs.
     */
    public static List<RatAnomalyModel> getCurrentReadabilityAnomaliesFromHTML(String html) throws IOException {
        return getReadabilityAnomaliesFromHTML(html, "#readability-current-anomalies-table-body");
    }

    /**
     * Get the as false positive detected anomalies from the HTML report.
     * 
     * @param html
     *            the HTML string of the report.
     * @return the false positive anomalies.
     * @throws IOException
     *             if an I/O error occurs.
     */
    public static List<RatAnomalyModel> getFalsePositiveReadabilityAnomaliesFromHTML(String html) throws IOException {
        return getReadabilityAnomaliesFromHTML(html, "#readability-false-positive-anomalies-table-body");
    }

    /**
     * Get the incorporated anomalies from the HTML report.
     * 
     * @param html
     *            the HTML string of the report.
     * @return the incorporated anomalies.
     * @throws IOException
     *             if an I/O error occurs.
     */
    public static List<RatAnomalyModel> getIncoporatedReadabilityAnomaliesFromHTML(String html) throws IOException {
        return getReadabilityAnomaliesFromHTML(html, "#readability-incorporated-anomalies-table-body");
    }

    private static List<RatAnomalyModel> getReadabilityAnomaliesFromHTML(String html, String tbodyId)
            throws IOException {
        List<RatAnomalyModel> anomalies = new ArrayList<RatAnomalyModel>();
        Document doc = Jsoup.parse(html);
        Elements table = doc.select(tbodyId + " tr");
        Elements trs = table.select("tr");

        for (Element tr : trs) {
            RatAnomalyModel anomaly = new RatAnomalyModel();
            Elements tds = tr.select("td");
            anomaly.setHashCode(Integer.valueOf(tr.attr("data-hash-code")));
            anomaly.setAnomalyName(tds.get(1).text());
            anomaly.setSeverity(tds.get(2).text());
            anomaly.setCoveredText(tds.get(3).text());
            anomaly.setSentence(tds.get(4).text());

            anomalies.add(anomaly);
        }

        return anomalies;
    }
}