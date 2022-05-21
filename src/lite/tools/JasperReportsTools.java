package lite.tools;

import java.io.FileOutputStream;
import java.util.Collection;
import java.util.Map;

import net.sf.jasperreports.engine.JRAbstractExporter;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRCsvExporterContext;
import net.sf.jasperreports.engine.export.JRGraphics2DExporter;
import net.sf.jasperreports.engine.export.JRGraphics2DExporterContext;
import net.sf.jasperreports.engine.export.JRHtmlExporterContext;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRPdfExporterContext;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.engine.export.JRRtfExporterContext;
import net.sf.jasperreports.engine.export.JRTextExporter;
import net.sf.jasperreports.engine.export.JRTextExporterContext;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterContext;
import net.sf.jasperreports.engine.export.JsonExporter;
import net.sf.jasperreports.engine.export.JsonExporterContext;
import net.sf.jasperreports.engine.export.oasis.JROdtExporter;
import net.sf.jasperreports.engine.export.oasis.JROdtExporterContext;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporterContext;
import net.sf.jasperreports.engine.export.ooxml.JRPptxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRPptxExporterContext;
import net.sf.jasperreports.export.CsvExporterConfiguration;
import net.sf.jasperreports.export.CsvReportConfiguration;
import net.sf.jasperreports.export.DocxExporterConfiguration;
import net.sf.jasperreports.export.DocxReportConfiguration;
import net.sf.jasperreports.export.Exporter;
import net.sf.jasperreports.export.Graphics2DExporterConfiguration;
import net.sf.jasperreports.export.Graphics2DExporterOutput;
import net.sf.jasperreports.export.Graphics2DReportConfiguration;
import net.sf.jasperreports.export.HtmlExporterConfiguration;
import net.sf.jasperreports.export.HtmlExporterOutput;
import net.sf.jasperreports.export.HtmlReportConfiguration;
import net.sf.jasperreports.export.JsonExporterConfiguration;
import net.sf.jasperreports.export.JsonExporterOutput;
import net.sf.jasperreports.export.JsonReportConfiguration;
import net.sf.jasperreports.export.OdtExporterConfiguration;
import net.sf.jasperreports.export.OdtReportConfiguration;
import net.sf.jasperreports.export.OutputStreamExporterOutput;
import net.sf.jasperreports.export.PdfExporterConfiguration;
import net.sf.jasperreports.export.PdfReportConfiguration;
import net.sf.jasperreports.export.PptxExporterConfiguration;
import net.sf.jasperreports.export.PptxReportConfiguration;
import net.sf.jasperreports.export.RtfExporterConfiguration;
import net.sf.jasperreports.export.RtfReportConfiguration;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.TextExporterConfiguration;
import net.sf.jasperreports.export.TextReportConfiguration;
import net.sf.jasperreports.export.WriterExporterOutput;
import net.sf.jasperreports.export.XlsExporterConfiguration;
import net.sf.jasperreports.export.XlsReportConfiguration;

public class JasperReportsTools {

	public enum ExportType {
		Csv, Docx, Graphics2D, Html, Json, Odt, Pdf, Pptx, Rtf, Text, Xls
	}

	public static void exportPdf(String jasperPath, String outPath, Map<String, Object> parameters, Collection<?> beanCollection) {

		try (FileOutputStream fos = new FileOutputStream(outPath, false)) {

			JRDataSource dataSource = new JRBeanCollectionDataSource(beanCollection);

			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperPath, parameters, dataSource);

			SimpleExporterInput input = new SimpleExporterInput(jasperPrint);

			SimpleOutputStreamExporterOutput out = new SimpleOutputStreamExporterOutput(fos);

			JRPdfExporter exporter = new JRPdfExporter();

			exporter.setExporterInput(input);

			exporter.setExporterOutput(out);

			exporter.exportReport();

		} catch (Exception e) {

			throw new RuntimeException(e);

		}

	}

	public static Exporter<?, ?, ?, ?> getExporter(ExportType exportType) throws JRException {
		switch (exportType) {
		case Csv:
			return new JRCsvExporter();
		case Docx:
			return new JRDocxExporter();
		case Graphics2D:
			return new JRGraphics2DExporter();
		case Html:
			return new HtmlExporter();
		case Json:
			return new JsonExporter();
		case Odt:
			return new JROdtExporter();
		case Pdf:
			return new JRPdfExporter();
		case Pptx:
			return new JRPptxExporter();
		case Rtf:
			return new JRRtfExporter();
		case Text:
			return new JRTextExporter();
		case Xls:
			return new JRXlsExporter();
		default:
			return null;
		}
	}

	/** 所有可用的 Exporters */
	public static class Exporters {

		public static JRAbstractExporter<CsvReportConfiguration, CsvExporterConfiguration, WriterExporterOutput, JRCsvExporterContext> getJRCsvExporter() {
			return new JRCsvExporter();
		}

		public static JRAbstractExporter<DocxReportConfiguration, DocxExporterConfiguration, OutputStreamExporterOutput, JRDocxExporterContext> getJRDocxExporter() {
			return new JRDocxExporter();
		}

		public static JRAbstractExporter<Graphics2DReportConfiguration, Graphics2DExporterConfiguration, Graphics2DExporterOutput, JRGraphics2DExporterContext> getJRGraphics2DExporter() throws JRException {
			return new JRGraphics2DExporter();
		}

		public static JRAbstractExporter<HtmlReportConfiguration, HtmlExporterConfiguration, HtmlExporterOutput, JRHtmlExporterContext> getHtmlExporter() {
			return new HtmlExporter();
		}

		public static JRAbstractExporter<JsonReportConfiguration, JsonExporterConfiguration, JsonExporterOutput, JsonExporterContext> getJsonExporter() {
			return new JsonExporter();
		}

		public static JRAbstractExporter<OdtReportConfiguration, OdtExporterConfiguration, OutputStreamExporterOutput, JROdtExporterContext> getJROdtExporter() {
			return new JROdtExporter();
		}

		public static JRAbstractExporter<PdfReportConfiguration, PdfExporterConfiguration, OutputStreamExporterOutput, JRPdfExporterContext> getJRPdfExporter() {
			return new JRPdfExporter();
		}

		public static JRAbstractExporter<PptxReportConfiguration, PptxExporterConfiguration, OutputStreamExporterOutput, JRPptxExporterContext> getJRPptxExporter() {
			return new JRPptxExporter();
		}

		public static JRAbstractExporter<RtfReportConfiguration, RtfExporterConfiguration, WriterExporterOutput, JRRtfExporterContext> getJRRtfExporter() {
			return new JRRtfExporter();
		}

		public static JRAbstractExporter<TextReportConfiguration, TextExporterConfiguration, WriterExporterOutput, JRTextExporterContext> getJRTextExporter() {
			return new JRTextExporter();
		}

		public static JRAbstractExporter<XlsReportConfiguration, XlsExporterConfiguration, OutputStreamExporterOutput, JRXlsExporterContext> getJRXlsExporter() {
			return new JRXlsExporter();
		}

	}

}
