package com.nex.task.tanning.actions;

import com.nex.communication.NexHelper;
import com.nex.communication.message.request.MuleRequest;
import com.nex.script.handler.TaskHandler;
import com.nex.task.mule.DepositToPlayerTask;
import com.nex.task.mule.PrepareForMuleDepositTask;
import com.nex.task.mule.WithdrawFromPlayerTask;
import com.nex.task.tanning.TanningTask;
import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.Bank;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;

public class BankLeatherWithdrawCowhide extends Task {

    private TanningTask taskRunner;
    public BankLeatherWithdrawCowhide(TanningTask taskRunner) {
        this.taskRunner = taskRunner;
    }

    @Override
    public boolean validate() {
        return Conditions.atBank() && !Conditions.gotCowhide() || !Conditions.gotEnoughCoins();
    }

    @Override
    public int execute() {
        if (Bank.isOpen()) {
            Time.sleepUntil(() -> Bank.getCount() > 0, 3000);
            // got something other than coins
            if (Conditions.gotJunkOrLeather()) {
                Time.sleep(100, 300);
                if (Bank.depositAllExcept(TanningTask.ANY_HIDE_RAW.or((i)->i.getId() == 995))) {
                    Time.sleepUntil(() -> !Conditions.gotJunkOrLeather(), 2000);
                }
                return Random.nextInt(100, 220);
            }

            // handle coins
            final Item coinsInventory = Inventory.getFirst("Coins");
            final int coinsAmount = coinsInventory != null ? coinsInventory.getStackSize() : 0;

            if (!Conditions.gotEnoughCoins()) {
                Item coinsInBank = Bank.getFirst("Coins");
                if (coinsInBank == null) {
                    // not enough coins to continue
                    Log.info("Out of coins");
                    withdrawHides();
                    return 2000;
                } else {
                    // need more coins
                    Bank.withdrawAll("Coins");
                    Time.sleepUntil(Conditions::gotEnoughCoins, 2000);
                    return Random.nextInt(200, 420);
                }
            }
            Log.info("Coins left", coinsAmount);

            Item cowhide = Bank.getFirst(TanningTask.ANY_HIDE);
            int cowhideBankAmount = cowhide == null ? 0 : cowhide.getStackSize();

            if (cowhideBankAmount >= 1) {
                // bank has more cowhide, withdraw Cowhide
                if (Bank.withdrawAll(cowhide.getId())) {
                    Time.sleepUntil(Conditions::gotCowhide, 2000);
                    return Random.nextInt(80, 160);
                } else {
                    Log.severe("Cowhide withdraw failed, retrying...");
                    return 400;
                }
            } else {
                // not enough cowhide to continue
                Log.info(cowhideBankAmount);
                Log.info("Out of cowhide");
                for (Item item : Bank.getItems(TanningTask.ANY_TANNED)){
                    int count = Bank.getCount(item.getId());
                    if (count > 0)
                        NexHelper.pushMessage(new MuleRequest("MULE_DEPOSIT:" + item.getId() + ":" + count));
                }
                withdrawHides();
                //NexHelper.pushMessage(new MuleRequest("MULE_WITHDRAW:995:" + taskRunner.WithdrawQty));
                return 2000;
            }
        } else {
            Bank.open();
            return Random.nextInt(400, 600);
        }
    }

    void withdrawHides(){
        NexHelper.pushMessage(new MuleRequest("MULE_WITHDRAW:" + taskRunner.HideID + ":" + taskRunner.WithdrawQty));
        Time.sleepUntil(() -> TaskHandler.getCurrentTask() != null
                && (TaskHandler.getCurrentTask() instanceof WithdrawFromPlayerTask || TaskHandler.getCurrentTask() instanceof DepositToPlayerTask), 20000);
    }
}
