package lib.ldd.data;

import lib.util.array.ArrayUtil;

public class GeometryWithMaterial {
	public final VBOContents[] geometry;
	public final Material material;

	public GeometryWithMaterial(VBOContents geometry, Material material) {
		this.geometry = new VBOContents[]{geometry};
		this.material = material;
	}
	
	public GeometryWithMaterial(VBOContents[] geometry, Material material) {
		this.material = material;
		this.geometry = geometry;
	}

	public GeometryWithMaterial merge(VBOContents combo) {
		VBOContents[] appendedContents = ArrayUtil.concat(geometry, new VBOContents[]{combo});
		return new GeometryWithMaterial(appendedContents, material);
	}

	public GeometryWithMaterial merge(GeometryWithMaterial combo) {
		VBOContents[] appendedContents = ArrayUtil.concat(geometry, combo.geometry);
		return new GeometryWithMaterial(appendedContents, material);
	}
}
