package tests.usecase;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import domain.Company;
import domain.assembly.assemblyline.AssemblyLine;
import domain.assembly.assemblyline.DoesNotExistException;
import domain.assembly.workstations.AssemblyTask;
import domain.assembly.workstations.VehicleAssemblyProcess;
import domain.assembly.workstations.Workstation;
import domain.user.Mechanic;

public class CarMechanicUseCaseTest {

	private Company company;

	@Before
	public void test() {
		this.company = new Company();
	}

	@Test
	public void testPerformAssemblyTaskHandler() throws DoesNotExistException {
		AssemblyLine assemblyLine = company.getAssemblyLines().get(0);
		Workstation w1 = assemblyLine.selectWorkstationById(1);
		Mechanic mechanic = new Mechanic(1);
		w1.addMechanic(mechanic);
		VehicleAssemblyProcess firstVAP = w1.getVehicleAssemblyProcess();
		for (AssemblyTask task : w1.getAllPendingTasks()) {
			w1.selectTask(task);
			w1.completeTask(mechanic, 60);
		}
		assertEquals(firstVAP, w1.getVehicleAssemblyProcess());
		
		Workstation w2 = assemblyLine.selectWorkstationById(2);
	}

}
