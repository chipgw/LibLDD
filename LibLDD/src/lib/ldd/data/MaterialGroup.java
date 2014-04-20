package lib.ldd.data;

public class MaterialGroup {
	public final Material material;
	public final VBOContents[] geometry;

	public MaterialGroup(Material material, VBOContents[] geometry) {
		this.material = material;
		this.geometry = geometry;
	}
}
