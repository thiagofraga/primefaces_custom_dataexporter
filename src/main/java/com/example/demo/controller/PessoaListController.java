package com.example.demo.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.RequestAction;
import org.ocpsoft.rewrite.el.ELBeanName;
import org.ocpsoft.rewrite.faces.annotation.Deferred;
import org.ocpsoft.rewrite.faces.annotation.IgnorePostback;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.example.demo.model.Pessoa;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

@Scope(value = "session")
@Component(value = "pessoaListController")
@ELBeanName(value = "pessoaListController")
@Join(path = "/pessoa", to = "/pessoa-list.jsf")
public class PessoaListController {

	private List<Pessoa> pessoas;

    @Deferred
    @RequestAction
    @IgnorePostback
    public void loadPessoas() {
    	pessoas = new ArrayList<>();
    	pessoas.add(new Pessoa(1L, "Thiago Fraga", "999.999.999-91"));
    	pessoas.add(new Pessoa(2L, "José da Silva", "999.999.999-92"));
    	pessoas.add(new Pessoa(3L, "Maria da Penha", "999.999.999-93"));
    	pessoas.add(new Pessoa(4L, "Fernandinho", "999.999.999-94"));
    }
    
    public void preProcessPDF(Object document) {
        Document pdf = (Document) document;
        pdf.setPageSize(PageSize.A4.rotate());
    }
    
    public void customPDF() throws DocumentException, IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        Document document = new Document();
        document.setPageSize(PageSize.A4.rotate());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, baos);
        if (!document.isOpen()) {
            document.open();
        }
        PdfPTable pdfTable = exportPDFTable();
        document.add(pdfTable);
        document.close();
        String fileName = "pessoas";
        writePDFToResponse(context.getExternalContext(), baos, fileName);
        context.responseComplete();
    }
    
    private PdfPTable exportPDFTable() throws DocumentException {
        PdfPTable pdfTable = new PdfPTable(3);
        pdfTable.setWidthPercentage(100);
        pdfTable.addCell(new Paragraph("ID"));
        pdfTable.addCell(new Paragraph("Nome"));
        pdfTable.addCell(new Paragraph("CPF"));
        for(Pessoa pessoa : pessoas) {
            pdfTable.addCell(new Paragraph(String.valueOf(pessoa.getId())));
            pdfTable.addCell(new Paragraph(pessoa.getNome()));
            pdfTable.addCell(new Paragraph(pessoa.getCpf()));
        }
        pdfTable.setWidths(new int[] {1, 3, 1});
        return pdfTable;
    }
    
    private void writePDFToResponse(ExternalContext externalContext, ByteArrayOutputStream baos, String fileName) throws IOException {
        externalContext.responseReset();
        externalContext.setResponseContentType("application/pdf");
        externalContext.setResponseHeader("Expires", "0");
        externalContext.setResponseHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        externalContext.setResponseHeader("Pragma", "public");
        externalContext.setResponseHeader("Content-disposition", "attachment;filename=" + fileName + ".pdf");
        externalContext.setResponseContentLength(baos.size());
        OutputStream out = externalContext.getResponseOutputStream();
        baos.writeTo(out);
        externalContext.responseFlushBuffer();
    }
    
	public List<Pessoa> getPessoas() {
		return pessoas;
	}

	public void setPessoas(List<Pessoa> pessoas) {
		this.pessoas = pessoas;
	}

}
