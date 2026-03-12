package com.wms.backend.service;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import com.wms.backend.domain.model.Invoice;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class PdfInvoiceService {

    public byte[] renderInvoicePdf(Invoice invoice) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document doc = new Document();
            PdfWriter.getInstance(doc, out);
            doc.open();

            Font title = new Font(Font.HELVETICA, 16, Font.BOLD);
            doc.add(new Paragraph("Waste Management Invoice", title));
            doc.add(new Paragraph(" "));
            doc.add(new Paragraph("Invoice ID: " + invoice.getId()));
            doc.add(new Paragraph("Month: " + invoice.getYearMonth()));
            doc.add(new Paragraph("LGA: " + invoice.getLga()));
            doc.add(new Paragraph("Address: " + invoice.getAddress().getDisplay()));
            doc.add(new Paragraph(" "));
            doc.add(new Paragraph("Total: NGN " + invoice.getTotalAmount()));
            doc.add(new Paragraph("Paid:  NGN " + invoice.getAmountPaid()));
            doc.add(new Paragraph("Owing: NGN " + invoice.getAmountOwing()));
            doc.add(new Paragraph("Due date: " + invoice.getDueDate()));
            doc.add(new Paragraph("Status: " + invoice.getStatus().name()));

            doc.close();
            return out.toByteArray();
        } catch (DocumentException e) {
            throw new IllegalStateException("Failed to render invoice PDF", e);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to render invoice PDF", e);
        }
    }
}

