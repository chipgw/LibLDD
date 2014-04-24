package lib.ldd.data;

public class Material {

	public final int id;
	public final int red;
	public final int green;
	public final int blue;
	public final int alpha;
	public final MaterialType type;

	public Material(int id, int red, int green, int blue, int alpha, MaterialType type) {
		this.id = id;
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
		this.type = type;
	}

}
