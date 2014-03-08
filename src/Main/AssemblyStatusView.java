package Main;

public class AssemblyStatusView {
	
	private String header;
	private returnType status;
	public AssemblyStatusView(String header, returnType status){
		this.header = header;
		this.status = status;
	}
	
	public int[] getAllWorkstationIds(){
		throw new UnsupportedOperationException();
	}
	
	public int getCarOrderIdAt(int workstationId){
		throw new UnsupportedOperationException();
	}
	
	public String[] getAllTasksAt(int workstationId){
		throw new UnsupportedOperationException();
	}
	
	public boolean taskIsDoneAt(String task,int workstationId){
		throw new UnsupportedOperationException();
	}

	public String getHeader() {
		return header;
	}
}
