package sim.agent;

public class LimitedMemory extends Memory {

	//Bounds of this memory's viewpoint onto the map
	private int xLeft;
	private int xRight;
	private int yTop;
	private int yBottom;
	private int offset;
	
	/**
	 * Construct a limited memory space
	 * @param limitedW 
	 * @param limitedH - Dimensions
	 * @param sensorRange - to calculate offset for setting cells, etc
	 */
	public LimitedMemory(int limitedW, int limitedH, int sensorRange) {
		super(limitedW, limitedH);
		this.xLeft = 0;
		this.xRight = sensorRange+1;
		this.yTop  = 0;
		this.yBottom = sensorRange+1;
		this.offset = sensorRange;
	}
	
	@Override
	public void rePlot(short[] newPos){
		short[][] newMap = new short[this.mem_height][this.mem_width];
		
		//New spatial bounds
		int newXLeft = newPos[0]-this.offset;
		int xRight   = newPos[0]+this.offset;
		int newYTop  = newPos[1]-this.offset;
		int yBottom  = newPos[1]+this.offset;
		
		for (int y = newYTop; y <= yBottom; y++){
			for (int x = newXLeft; x <= xRight; x++){
				if (this.inSpatialBounds(x, y)){
					//New cell is in old viewpoint
					short cell = this.readSquare(x, y);
					this.setDirect(x-newXLeft, y-newYTop, cell);
				} else {
					//not in old viewpoint, so is unknown
					this.setUnsensed(x-newXLeft, y-newYTop);
				}
			}
		}
		//Set bounds of new viewpoint
		this.xLeft = newXLeft;
		this.xRight = xRight;
		this.yTop = newYTop;
		this.yBottom = yBottom;
		//this.map = newMap;
	}
	
	private boolean inSpatialBounds(int x, int y){
		if ((x<this.xLeft) || (x>this.xRight)) {
			return false;
		}
		if ((y<this.yTop) || (y>this.yBottom)){
			return false;
		}
		return true;
	}
	
	//Set directly into the limited memory without offsetting
	private void setDirect(int x, int y, short val){
		this.map[y][x]=val;
	}
	
	@Override
	public void setCell(int x, int y, short val){
		try{
			super.setCell(x-this.xLeft, y-this.yTop, val);
		} catch (ArrayIndexOutOfBoundsException e){
			System.out.printf("%d,%d :: %d,%d\n", x, y, this.xLeft, this.yTop);
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	@Override
	public boolean validPosition(int x, int y){
		return super.validPosition(x-this.xLeft, y-this.yTop);
	}
	
	@Override
	public boolean occupied(int x, int y){
		if (!this.inSpatialBounds(x, y)){
			return false;
		}
		return super.occupied(x-this.xLeft, y-this.yTop);
	}
	
	@Override
	public short readSquare(int x, int y){
		if (!this.inSpatialBounds(x, y)){
			return 99;
		}
		return super.readSquare(x-this.xLeft, y-this.yTop);
	}
	
	@Override
	public boolean isGoal(int x, int y){
		return super.isGoal(x-this.xLeft, y-this.yTop);
	}
}
