package com.nex.task.actions.mule;




import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.Bank;
import org.rspeer.runetek.api.component.tab.Inventory;

import com.nex.communication.NexHelper;
import com.nex.communication.message.request.MuleRequest;
import com.nex.script.Nex;
import com.nex.script.handler.TaskHandler;
import com.nex.task.action.Action;
import com.nex.task.mule.DepositToPlayerTask;



public class PrepareForMuleDeposit extends Action {

	public boolean hasWithdrawnMoney = false;
	public int execute() {
		if (!hasWithdrawnMoney) {
			withdrawMoney();
		}else {
			hasWithdrawnMoney = false;
			NexHelper.pushMessage(new MuleRequest(
					"MULE_DEPOSIT:995:" + Inventory.getCount(true,995)));
			Time.sleepUntil(() -> TaskHandler.getCurrentTask() != null && TaskHandler.getCurrentTask().getClass().equals(DepositToPlayerTask.class), 20000);
			
		}
		return 200;
	}

	private void withdrawMoney() {
		try {
			if (Bank.open()) {
				if (Bank.getCount(995) == 0) {
					hasWithdrawnMoney = true;
				} else {
					Bank.withdrawAll(995);
				}

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}


	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

}
