package cn.nkpro.elcube.components.CR.pojo;

public class WeightCategory{

    private String category;//类别
    private int weight;//权重值

    public WeightCategory(String category, int weight) {
        this.category = category;
        this.weight = weight;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}