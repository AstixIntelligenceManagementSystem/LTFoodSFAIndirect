package project.astix.com.ltfoodsfaindirect.Models;

public class ActualVisitProductInfo {

    private String productName;
    private String storeId;
    private String productId;
    private int sStat;
    private String stock;
    private int isDefaultProduct;
    private String displayUnit;

    public ActualVisitProductInfo(String storeId, String productId, int sStat, String stock, int isDefaultProduct, String displayUnit) {
        this.storeId = storeId;
        this.productId = productId;
        this.sStat = sStat;
        this.stock = stock;
        this.isDefaultProduct = isDefaultProduct;
        this.displayUnit = displayUnit;
    }

    public ActualVisitProductInfo(String productName, String productId, String displayUnit) {
        this.productName = productName;
        this.productId = productId;
        this.displayUnit = displayUnit;
    }

    public void setIsDefaultProduct(int isDefaultProduct) {
        this.isDefaultProduct = isDefaultProduct;
    }

    public String getProductName() {
        return productName;
    }

    public String getStoreId() {
        return storeId;
    }

    public String getProductId() {
        return productId;
    }

    public int getsStat() {
        return sStat;
    }

    public String getStock() {
        return stock;
    }

    public int getIsDefaultProduct() {
        return isDefaultProduct;
    }

    public String getDisplayUnit() {
        return displayUnit;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    @Override
    public String toString() {
        return "ActualVisitProductInfo{" +
                "productName='" + productName + '\'' +
                ", storeId='" + storeId + '\'' +
                ", productId='" + productId + '\'' +
                ", sStat=" + sStat +
                ", stock='" + stock + '\'' +
                ", isDefaultProduct=" + isDefaultProduct +
                ", displayUnit='" + displayUnit + '\'' +
                '}';
    }
}
