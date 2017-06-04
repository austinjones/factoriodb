package com.factoriodb.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ItemsStack implements Iterable<ItemStack> {
	private List<ItemStack> items;

	public ItemsStack() {
		this.items = new ArrayList<>();
	}

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (!(obj instanceof ItemsStack)) {
            return false;
        }

        ItemsStack other = (ItemsStack)obj;
        for(ItemStack items : this.items) {
            String item = items.name();
            if (!items.equals(other.get(item))) {
                return false;
            }
        }

        for(ItemStack items : other.items) {
            String item = items.name();
            if (!items.equals(this.get(item))) {
                return false;
            }
        }

        return true;
    }

    public ItemsStack(ItemStack[] items) {
		this.items = new ArrayList<>(Arrays.asList(items));
	}
	public ItemsStack(List<ItemStack> item) {
		this(item.toArray(new ItemStack[item.size()]));
	}

	public ItemsStack(ItemStack item) {
		this.items = new ArrayList<>();
        this.items.add(item);
	}

	public ItemsStack(String item, double stackSize) {
		this(new ItemStack(item, stackSize));
	}

	public ItemsStack(Map<String, Double> input) {
        this.items = new ArrayList<>();
		for(Map.Entry<String, Double> entry : input.entrySet() ) {
            this.items.add(new ItemStack(entry.getKey(), entry.getValue()));
		}
	}

	public List<ItemStack> items() {
		return items;
	}

	public static ItemsStack add(ItemsStack stack1, ItemsStack stack2) {
		Map<String, Double> stacks = stack1.toMap();
		Map<String, Double> stacks2 = stack2.toMap();
		for(Map.Entry<String, Double> entry : stacks2.entrySet() ) {
			Double val = stacks.get(entry.getKey());
			if(val != null) {
				double newval = val + entry.getValue();
				stacks.put(entry.getKey(), newval);
			} else {
                stacks.put(entry.getKey(), entry.getValue());
			}
		}

		return new ItemsStack(stacks);
	}

	private Map<String, Double> toMap() {
		Map<String, Double> map = new HashMap<>();
		for(ItemStack item : items) {
			Double val = map.get(item.name());
			if(val != null) {
				double newval = val + item.amount();
				map.put(item.name(), newval);
			} else {
				map.put(item.name(), item.amount());
			}
		}

		return map;
	}

	public ItemStack get(String name) {
		for(ItemStack item : items) {
			if(name.equals(item.name())) {
				return item;
			}
		}

		return null;
	}

	public double getDouble(String name) {
		ItemStack item = get(name);

		if(item == null) {
			return 0;
		}

		return item.amount();
	}

	public double total() {
		double sum = 0;

		for (ItemStack f : items) {
			sum += f.amount();
		}

		return sum;
	}

	@Override
	public String toString() {
		return items.toString();
	}

	public static ItemsStack sub(ItemsStack item1, ItemsStack item2) {
		Map<String, Double> items = item1.toMap();
		Map<String, Double> items2 = item2.toMap();
		for(Map.Entry<String, Double> entry : items2.entrySet() ) {
			Double val = items.get(entry.getKey());
			if(val != null) {
				double newval = val - entry.getValue();
				items.put(entry.getKey(), newval);
			} else {
				items.put(entry.getKey(), entry.getValue());
			}
		}

		return new ItemsStack(items);
	}

	public boolean anyMatch(Predicate<ItemStack> pred) {
		return items.stream().anyMatch(pred);
	}

	public double totalIf(Predicate<Double> pred) {
		double sum = 0;

		for (ItemStack f : items) {
			double item = f.amount();
			sum += pred.test(item) ? f.amount() : 0.0;
		}

		return sum;
	}

	public static ItemsStack mul(ItemsStack items, double d) {
        if(items == null) {
            return new ItemsStack();
        }

		List<ItemStack> newList = new ArrayList<>(items.items.size());

		for(ItemStack item : items) {
			String name = item.name();
			double rate = item.amount();
			newList.add(new ItemStack(name, d*rate));
		}

		return new ItemsStack(newList);
	}

	public ItemsStack throttle(ItemsStack input) {
		ArrayList<ItemStack> output = new ArrayList<>(this.items.size());

		for(ItemStack thisItem : this.items) {
			String item = thisItem.name();
			double thisRate = thisItem.amount();
			double limit = input.getDouble(item);
			double rate = Math.min(thisRate, limit);

			if(rate != 0) {
				output.add(new ItemStack(item, rate));
			}
		}

		return new ItemsStack(output);
	}

	public double minratio(ItemsStack input) {
		double minratio = Double.MAX_VALUE;

		for(ItemStack thisItem : this.items) {
			String item = thisItem.name();
			double other = input.getDouble(item);
			if (other == 0.0) {
				continue;
			}

			double ratio = thisItem.amount() / other;
			if(ratio < minratio) {
				minratio = ratio;
			}
		}

        if(minratio == Double.MAX_VALUE) {
            return 0;
        }

		return minratio;
	}

	public double maxratio(ItemsStack input) {
		double maxratio = Double.MIN_VALUE;

		for(ItemStack item : this.items()) {
			double other = input.getDouble(item.name());
			if (other == 0.0) {
				continue;
			}

			double ratio = item.amount() / other;
			if(ratio > maxratio) {
				maxratio = ratio;
			}
		}

        if(maxratio == Double.MIN_VALUE) {
            return 0;
        }

		return maxratio;
	}

	public static boolean gt(ItemsStack gt, ItemsStack lt) {
        if(gt == null) {
            return false;
        }

        if(lt == null) {
            return true;
        }

		for(ItemStack gtItem : gt.items) {
			if(gtItem.amount() < lt.getDouble(gtItem.name())) {
				return false;
			}
		}

		for(ItemStack ltItem : lt.items) {
			if(gt.getDouble(ltItem.name()) < ltItem.amount()) {
				return false;
			}
		}

		return true;
	}

    public static boolean lte(ItemsStack lte, ItemsStack gt) {
        return ItemsStack.gt(gt, lte);
    }

	public static ItemsStack max(ItemsStack... itemses) {
		HashMap<String, Double> output = new HashMap<>();
		for(ItemsStack items : itemses) {
			for(ItemStack item : items.items) {
				Double val = output.get(item.name());
				if(val == null) {
					val = item.amount();
					output.put(item.name(), val);
				} else if(item.amount() > val) {
					val = item.amount();
					output.put(item.name(), val);
				}
			}
		}

		return new ItemsStack(output);
	}

	public static ItemsStack min(ItemsStack... itemses) {
		HashMap<String, Double> output = new HashMap<>();
		for(ItemsStack items : itemses) {
			for(ItemStack item : items.items) {
				Double val = output.get(item.name());
				if(val == null) {
					val = item.amount();
					output.put(item.name(), val);
				} else if(item.amount() < val) {
					val = item.amount();
					output.put(item.name(), val);
				}
			}
		}

		return new ItemsStack(output);
	}

    public static ItemsStack div(ItemsStack input, float n) {
        if(n == 0) {
            throw new ArithmeticException("Divide by zero");
        }

        return mul(input, 1/n);
    }

    public ItemsStack filter(Collection<String> filter) {
        ArrayList<ItemStack> output = new ArrayList<>(this.items.size());

        for(ItemStack thisItem : this.items) {
            String item = thisItem.name();
            double rate = thisItem.amount();

            if(filter.contains(item) && rate != 0) {
                output.add(new ItemStack(item, rate));
            }
        }

        return new ItemsStack(output);
    }

    @Override
    public Iterator<ItemStack> iterator() {
        return items.iterator();
    }

    public Collection<String> itemNames() {
        return items().stream().map((e) -> e.name()).collect(Collectors.toSet());
    }
}
