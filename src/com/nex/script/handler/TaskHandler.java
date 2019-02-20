package com.nex.script.handler;

import java.util.*;

import com.nex.task.IHandlerTask;
import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.ui.Log;

import com.nex.script.Nex;
import com.nex.script.items.RSItem;
import com.nex.task.NexTask;

public class TaskHandler {
	public static Stack<NexTask> available_tasks = new Stack<NexTask>();
	public static Deque<IHandlerTask> handler_tasks = new ArrayDeque<>();//A dequeue offers items in a 3,2,1,0 when enumerated
	public static Stack<NexTask> old_tasks = new Stack<NexTask>();

	public static NexTask PREVIOUS_TASK;
	private static NexTask CURRENT_TASK = null;

	public static void addTask(NexTask task) {
		available_tasks.add(task);
	}
	public static void addHandler(IHandlerTask task){
		handler_tasks.push(task);
	}

	/*
	 * Adds a task to the top of the list. If currenttask is not null, remove
	 * currenttask and add it to the list
	 */
	public static void addPrioritizedTask(NexTask task) {
		NexTask currentNexTask;
		old_tasks.push(task);
		if (CURRENT_TASK != null) {
			currentNexTask = CURRENT_TASK;

			available_tasks.push(currentNexTask);
			removeTask();
		}
		CURRENT_TASK = task;
	}

	/*
	 * Adds a task to the top of the list. If currenttask is not null, remove
	 * currenttask and add it to the list
	 */
	public static void addTaskAndResetStack(NexTask task) {
		removeTask();
		available_tasks.removeAllElements();
		CURRENT_TASK = task;
		old_tasks.push(task);
	}

	public static boolean popTask() {
		if (available_tasks.isEmpty()) {
			return false;
		}
		CURRENT_TASK = available_tasks.pop();
		return true;
	}

	public static String getTaskList() {
		String tempString = "";
		if (available_tasks.isEmpty()) {
			return "No NexTasks in NexTaskList.";
		} else {
			for (NexTask task : available_tasks) {
				tempString += task;
			}
		}
		return tempString;
	}

	public static boolean canSellItem(Item requestedItem) {
		Log.fine("checking if we can sell: " + requestedItem.getName());
		List<RSItem> itemsToNotSell = new ArrayList<RSItem>();
		if (CURRENT_TASK != null) {
			Log.fine("we have current task." + CURRENT_TASK.getRequiredItems().size());
		}
		if (PREVIOUS_TASK != null) {
			Log.fine("previous task." + PREVIOUS_TASK.getRequiredItems().size());
		}
		if (CURRENT_TASK != null && CURRENT_TASK.getRequiredItems() != null) {
			itemsToNotSell.addAll(CURRENT_TASK.getRequiredItems());
		}

		if (PREVIOUS_TASK != null && PREVIOUS_TASK.getRequiredItems() != null) {
			itemsToNotSell.forEach(item -> {
				Log.fine("ITEM: " + item);
			});
		}
		Log.fine("Items not to sell length: " + itemsToNotSell.size());
		for (RSItem unsellableItem : itemsToNotSell) {

			if (unsellableItem != null) {
				Log.fine("unsellable: " + unsellableItem.getName());

				if (unsellableItem.getName().equals(requestedItem.getName())) {
					Log.info("CANNOT SELL ITEM: " + requestedItem.getName());
					Log.fine("CANNOT SELL ITEM: " + requestedItem.getName());

					return false;
				}
			} else {
				Log.fine("null object to sell");
			}
		}
		return true;
	}

	public static NexTask getCurrentTask() {
		return CURRENT_TASK;
	}
	public static IHandlerTask getLatesthandler(){
		IHandlerTask result = null;
		for (IHandlerTask task : handler_tasks) {
			if (!task.isFinished()) {
				result = task;
				break;
			}
		}
		if (CURRENT_TASK instanceof IHandlerTask){
			IHandlerTask task = (IHandlerTask)CURRENT_TASK;
			if(result == null || task.getTimeStarted() > result.getTimeStarted() && !task.isFinished())
				result = task;
		}
		return result;
	}

	public static void removeTask() {
		if (CURRENT_TASK != null) {
			Log.fine("removing obj");
			Log.fine("SIZE:" + CURRENT_TASK.getRequiredItems().size());
			if (!old_tasks.isEmpty()) {
				PREVIOUS_TASK = old_tasks.peek();
			}
			Log.fine("CLONED OBJECT:" + PREVIOUS_TASK.getClass().getSimpleName());
			Log.fine("SIZE:" + PREVIOUS_TASK.getRequiredItems().size());

			CURRENT_TASK.removeTask();
			CURRENT_TASK = null;
		}
	}
	public static void removeHandler(IHandlerTask task){
		handler_tasks.remove(task);
	}

}
