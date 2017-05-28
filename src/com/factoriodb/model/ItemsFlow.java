package com.factoriodb.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.stringtemplate.v4.misc.ArrayIterator;

public class ItemsFlow {
	private ItemFlow[] flows;
	
	public ItemsFlow() {
		this.flows = new ItemFlow[0];
	}
	
	public ItemsFlow(ItemFlow[] flows) {
		this.flows = flows;
	}
	public ItemsFlow(List<ItemFlow> flow) {
		this(flow.toArray(new ItemFlow[flow.size()]));
	}
	
	public ItemsFlow(ItemFlow flow) {
		this.flows = new ItemFlow[] { flow };
	}
	
	public ItemsFlow(String item, double flowRate) {
		this(new ItemFlow(item, flowRate));
	}
	
	public ItemsFlow(Map<String, Double> input) {
		List<ItemFlow> result = new ArrayList<>();
		for(Map.Entry<String, Double> entry : input.entrySet() ) {
			result.add(new ItemFlow(entry.getKey(), entry.getValue()));
		}
		
		this.flows = result.toArray(new ItemFlow[result.size()]);
	}

	public ItemFlow[] flows() {
		return flows;
	}

	public static ItemsFlow add(ItemsFlow flow1, ItemsFlow flow2) {
		Map<String, Double> flows = flow1.toMap();
		Map<String, Double> flows2 = flow2.toMap();
		for(Map.Entry<String, Double> entry : flows2.entrySet() ) {
			Double val = flows.get(entry.getKey());
			if(val != null) {
				double newval = val + entry.getValue();
				flows.put(entry.getKey(), newval);
			} else {
				flows.put(entry.getKey(), entry.getValue());
			}
		}
		
		return new ItemsFlow(flows);
	}
	
	private Map<String, Double> toMap() {
		Map<String, Double> map = new HashMap<>();
		for(ItemFlow flow : flows) {
			Double val = map.get(flow.item());
			if(val != null) {
				double newval = val + flow.flow();
				map.put(flow.item(), newval);
			} else {
				map.put(flow.item(), flow.flow());
			}
		}
		
		return map;
	}
	
	public ItemFlow get(String name) {
		for(ItemFlow flow : flows) {
			if(name.equals(flow.item())) {
				return flow;
			}
		}
		
		return null;
	}

	public double getDouble(String name) {
		ItemFlow flow = get(name);
		
		if(flow == null) {
			return 0;
		}
		
		return flow.flow();
	}
	
	public double total() {
		double sum = 0;
		
		for (ItemFlow f : flows) {
			sum += f.flow();
		}
		
		return sum;
	}
	
	@Override
	public String toString() {
		return Arrays.toString(flows);
	}

	public static ItemsFlow sub(ItemsFlow flow1, ItemsFlow flow2) {
		Map<String, Double> flows = flow1.toMap();
		Map<String, Double> flows2 = flow2.toMap();
		for(Map.Entry<String, Double> entry : flows2.entrySet() ) {
			Double val = flows.get(entry.getKey());
			if(val != null) {
				double newval = val - entry.getValue();
				flows.put(entry.getKey(), newval);
			} else {
				flows.put(entry.getKey(), entry.getValue());
			}
		}
		
		return new ItemsFlow(flows);
	}

	public boolean anyMatch(Predicate<ItemFlow> pred) {
		List<ItemFlow> list = Arrays.asList(flows);
		return list.stream().anyMatch(pred);
	}

	public double totalIf(Predicate<Double> pred) {
		double sum = 0;
		
		for (ItemFlow f : flows) {
			double flow = f.flow();
			sum += pred.test(flow) ? f.flow() : 0.0;
		}
		
		return sum;
	}

	public static ItemsFlow mul(ItemsFlow output, double d) {
		ItemFlow[] newArray = new ItemFlow[output.flows.length];
		
		for(int i = 0; i < newArray.length; i++) {
			String item = output.flows[i].item();
			double rate = output.flows[i].flow();
			newArray[i] = new ItemFlow(item, d*rate);
		}
		
		return new ItemsFlow(newArray);
	}

	public ItemsFlow throttle(ItemsFlow input) {
		ArrayList<ItemFlow> output = new ArrayList<>(this.flows.length);
		
		for(int i = 0; i < this.flows.length; i++) {
			String item = this.flows[i].item();
			double thisRate = this.flows[i].flow();
			double limit = input.getDouble(item);
			double rate = Math.min(thisRate, limit);
			
			if(rate != 0) {
				output.add(new ItemFlow(item, rate));
			}
		}
		
		return new ItemsFlow(output);
	}

	public double minratio(ItemsFlow input) {
		double minratio = Double.MAX_VALUE;
		
		for(int i = 0; i < this.flows.length; i++) {
			String item = this.flows[i].item();
			double other = input.getDouble(item);
			if (other == 0.0) {
				continue;
			}
			
			double ratio = this.flows[i].flow() / other;
			if(ratio < minratio) {
				minratio = ratio;
			}
		}
		
		return minratio;
	}
	
	public double maxratio(ItemsFlow input) {
		double maxratio = Double.MAX_VALUE;
		
		for(int i = 0; i < this.flows.length; i++) {
			String item = this.flows[i].item();
			double other = input.getDouble(item);
			if (other == 0.0) {
				continue;
			}
			
			double ratio = this.flows[i].flow() / other;
			if(ratio > maxratio) {
				maxratio = ratio;
			}
		}
		
		return maxratio;
	}

	public static boolean gt(ItemsFlow gt, ItemsFlow lt) {
		for(ItemFlow gtItem : gt.flows) {
			if(gtItem.flow() < lt.getDouble(gtItem.item())) {
				return false;
			}
		}
		
		for(ItemFlow ltItem : lt.flows) {
			if(gt.getDouble(ltItem.item()) < ltItem.flow()) {
				return false;
			}
		}
		
		return true;
	}
	
	public static ItemsFlow max(ItemsFlow... flows) {
		HashMap<String, Double> output = new HashMap<>();
		for(ItemsFlow flow : flows) {
			for(ItemFlow item : flow.flows) {
				Double val = output.get(item.item());
				if(val == null) {
					val = item.flow();
					output.put(item.item(), val);
				} else if(item.flow() > val) {
					val = item.flow();
					output.put(item.item(), val);
				}
			}
		}
		
		return new ItemsFlow(output);
	}
	
	public static ItemsFlow min(ItemsFlow... flows) {
		HashMap<String, Double> output = new HashMap<>();
		for(ItemsFlow flow : flows) {
			for(ItemFlow item : flow.flows) {
				Double val = output.get(item.item());
				if(val == null) {
					val = item.flow();
					output.put(item.item(), val);
				} else if(item.flow() < val) {
					val = item.flow();
					output.put(item.item(), val);
				}
			}
		}
		
		return new ItemsFlow(output);
	}
}
