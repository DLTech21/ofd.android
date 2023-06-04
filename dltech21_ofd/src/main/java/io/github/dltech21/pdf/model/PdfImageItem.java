package io.github.dltech21.pdf.model;

import com.itextpdf.text.Image;


public class PdfImageItem {
    private Image image;
    private int pageNum;

    /**
     *
     * @param image 图片
     * @param pageNum pdf页码由1开始
     */
    public PdfImageItem(Image image, int pageNum) {
        this.image = image;
        this.pageNum = pageNum;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }
}
