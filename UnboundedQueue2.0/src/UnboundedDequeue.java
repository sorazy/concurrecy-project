import java.util.concurrent.atomic.AtomicReference;

public class UnboundedDequeue<T> {
	public static Object left_null = new Object();
	public static Object right_null = new Object();
	public static Object left_seal = new Object();
	public static Object right_seal = new Object();
	// Set array size here
	public static final int array_size = 1000; 
	private AtomicReference<NodeHint<T>> leftNodeHint, rightNodeHint;

	public UnboundedDequeue() {
		this.leftNodeHint = new AtomicReference<>(new NodeHint<>(new Node<>(array_size / 2), 0));
		this.rightNodeHint = new AtomicReference<>(new NodeHint<>(this.leftNodeHint.get().buffer, 0));
		this.leftNodeHint.get().loc = this.leftNodeHint.get().buffer.leftHint;
		this.rightNodeHint.get().loc = this.rightNodeHint.get().buffer.rightHint;
	}
	
	@SuppressWarnings("unchecked")
	NodeHint<T> findLeftEdge(NodeHint<T> hint) {
		Node<T> node = hint.buffer;
		int index = hint.loc; 
		
		while((node.array[index - 1].get().data != left_null) 
				&& (node.array[index - 1].get().data != left_seal)) {
			index--;
			if(index == 0) {
				if (node.array[0].get().data instanceof Node<?>) {
					node =(Node<T>) node.array[0].get().data;
					index = array_size - 2;
				}
				else {
					System.out.println("find left problem");
				}
			}
		}
		if(index == array_size - 1) {
			if (node.array[array_size - 1].get().data instanceof Node<?>) {
				node =(Node<T>) node.array[array_size - 1].get().data;
				index = 1;
			} else {
				System.out.println("find left problem");
			}
		}
		while((node.array[index].get().data == left_null) 
				&& (node.array[index].get().data != left_seal)) {
			index++;
			if(index == array_size - 1) {
				if (node.array[array_size - 1].get().data instanceof Node<?>) {
					node =(Node<T>) node.array[array_size - 1].get().data;
					index = 1;
				} else {
					System.out.println("find left problem");
				}
			}
		}
		return new NodeHint<>(node, index);
	}
	@SuppressWarnings("unchecked")
	NodeHint<T> findRightEdge(NodeHint<T> hint) {
		Node<T> node = hint.buffer;
		int index = hint.loc;
		
		while((node.array[index + 1].get().data != right_null) 
				&& (node.array[index + 1].get().data != right_seal)) {
			index++;
			if(index == array_size - 1) {
				if (node.array[array_size - 1].get().data instanceof Node<?>) {
					node =(Node<T>) node.array[array_size - 1].get().data;
					index = 1;
				}
				else {
					System.out.println("find right problem");
				}
			}
		}
		if(index == 0) {
			if (node.array[0].get().data instanceof Node<?>) {
				node =(Node<T>) node.array[0].get().data;
				index = array_size - 2;
			} else {
				System.out.println("find right problem");
			}
		}
		while(node.array[index].get().data == right_null
				&& (node.array[index + 1].get().data != right_seal)) {
			index--;
			if(index == 0) {
				if (node.array[0].get().data instanceof Node<?>) {
					node =(Node<T>) node.array[0].get().data;
					index = array_size - 2;
				} else {
					System.out.println("find right problem");
				}
			}
		}
		return new NodeHint<>(node, index);
	}

	NodeHint<T> updateLeftHint(NodeHint<T> old, Node<T> newNode, int newIndex) {
		NodeHint<T> newHint = new NodeHint<>(newNode, newIndex);
		this.leftNodeHint.set(newHint);
		old.buffer.leftHint = newIndex;
		return newHint;
	}

	NodeHint<T> updateRightHint(NodeHint<T> old, Node<T> newNode, int newIndex) {
		NodeHint<T> newHint = new NodeHint<>(newNode, newIndex);
		this.rightNodeHint.set(newHint);
		old.buffer.rightHint = newIndex;
		return newHint;
	}

	@SuppressWarnings("unchecked")
	public void push_left(T data) {
		NodeHint<T> hintCopy;
		NodeHint<T> edge;
		Node<T> edgeNode;
		int edgeIndex;
		AtomicReference<DequeueSlot<Object>> in;
		DequeueSlot<Object> inCopy;
		AtomicReference<DequeueSlot<Object>> out;
		DequeueSlot<Object> outCopy;
		int count = 0;
		
		while(true) {

			count++;
			if(count > 1000) {
				count = count + 0;
			}
			
			hintCopy = this.leftNodeHint.get();
			edge = this.findLeftEdge(hintCopy);
			edgeNode = edge.buffer;
			edgeIndex = edge.loc;

			in = edgeNode.array[edgeIndex];
			inCopy = in.get();
			out = edgeNode.array[edgeIndex - 1];
			outCopy = out.get();

			if((inCopy.data == (T) left_null || inCopy.data == (T) right_seal)
					|| (edgeIndex != 1 && outCopy.data != (T) left_null)
					|| (edgeIndex == array_size - 1 && inCopy.data != (T) right_null))
				continue;

			// interior push
			if(edgeIndex != 1) {
				if(in.compareAndSet(inCopy, new DequeueSlot<>(inCopy.data, inCopy.count + 1))
						&& out.compareAndSet(outCopy, new DequeueSlot<>(data, outCopy.count + 1)))
					this.updateLeftHint(hintCopy, edgeNode, edgeIndex - 1);
				return;
			}
			// edge is straddling or on a boundary
			else {
				// check for boundary edge
				if(outCopy.data == left_null) {
					Node<T> newNode = new Node<>(array_size);
					newNode.array[array_size - 2].get().data = data;
					newNode.array[array_size - 1].get().data = edgeNode;

					if(in.compareAndSet(inCopy, new DequeueSlot<>(inCopy.data, inCopy.count + 1))
							&& out.compareAndSet(outCopy, new DequeueSlot<>(newNode, outCopy.count + 1))) {
						this.updateLeftHint(hintCopy, newNode, array_size - 2);
						return;
					}
				}

				else {
					Node<T> outNode = (Node<T>) outCopy.data; 

					AtomicReference<DequeueSlot<Object>> far = outNode.array[array_size - 2];
					DequeueSlot<Object> farCopy = far.get();
					
					AtomicReference<DequeueSlot<Object>> back = outNode.array[array_size - 1];
					DequeueSlot<Object> backCopy = back.get();

					if((Node<T>) backCopy.data != edgeNode) continue;

					// check state for straddling push
					if(farCopy.data == left_null) {
						if(in.compareAndSet(inCopy, new DequeueSlot<>(inCopy.data, inCopy.count + 1))
								&& far.compareAndSet(farCopy, new DequeueSlot<>(data, farCopy.count + 1))) {
							this.updateLeftHint(hintCopy, outNode, array_size - 2);
							return;
						}
					}
					else if(farCopy.data == left_seal) {
						if(in.compareAndSet(inCopy,  new DequeueSlot<>(inCopy.data, inCopy.count + 1))
								&& out.compareAndSet(outCopy, new DequeueSlot<>(left_null, outCopy.count + 1))) {
							this.updateLeftHint(hintCopy, edgeNode, 1);
							NodeHint<T> rightHint = this.findRightEdge(this.rightNodeHint.get());
							this.updateRightHint(rightHint, rightHint.buffer, rightHint.loc);
							// retire?
						}
					}
				} // end straddling edge
			} // end boundary or straddling edge
		} // end while
	} // end push_left

	@SuppressWarnings("unchecked")
	public void push_right(T data) {
		NodeHint<T> hintCopy;
		NodeHint<T> edge;
		Node<T> edgeNode;
		int edgeIndex;
		AtomicReference<DequeueSlot<Object>> in;
		DequeueSlot<Object> inCopy;
		AtomicReference<DequeueSlot<Object>> out;
		DequeueSlot<Object> outCopy;
		int count = 0;
		
		while(true) {
			
			
			count++;
			if(count > 1000) {
				count = count + 0;
			}
			
			
			hintCopy = this.rightNodeHint.get();
			edge = this.findRightEdge(hintCopy);
			edgeNode = edge.buffer;
			edgeIndex = edge.loc;

			in = edgeNode.array[edgeIndex];
			inCopy = in.get();
			out = edgeNode.array[edgeIndex + 1];
			outCopy = out.get();
			
			if((inCopy.data == (T) right_null || inCopy.data == left_seal)
					|| (edgeIndex != array_size - 2 && outCopy.data != right_null)
					|| (edgeIndex == 0 && inCopy.data != left_null))
				continue;
			
			
			// interior push
			if(edgeIndex != array_size - 2) {
				if(in.compareAndSet(inCopy, new DequeueSlot<>(inCopy.data, inCopy.count + 1))
						&& out.compareAndSet(outCopy, new DequeueSlot<>(data, outCopy.count + 1)))
					this.updateRightHint(hintCopy, edgeNode, edgeIndex + 1);
				return;
			}
			// edge is straddling or on a boundary
			else {
				// check for boundary edge
				if(outCopy.data == (T) right_null) {
					Node<T> newNode = new Node<>(0);
					newNode.array[1].get().data = data;
					newNode.array[0].get().data = edgeNode;

					if(in.compareAndSet(inCopy, new DequeueSlot<>(inCopy.data, inCopy.count + 1))
							&& out.compareAndSet(outCopy, new DequeueSlot<>(newNode, outCopy.count + 1))) {
						this.updateRightHint(hintCopy, newNode, 1);
						return;
					}
				}
				else {
					Node<T> outNode = (Node<T>) outCopy.data; 
//					AtomicReference<DequeueSlot<Object>> far = ((Node<T>) outCopy.data).array[1];
//					DequeueSlot<Object> farCopy = far.get();
					AtomicReference<DequeueSlot<Object>> far = outNode.array[1];
					DequeueSlot<Object> farCopy = far.get();

					AtomicReference<DequeueSlot<Object>> back = outNode.array[0];
					DequeueSlot<Object> backCopy = back.get();

					if((Node<T>) backCopy.data != edgeNode) continue;

					// check state for straddling push
					if(farCopy.data == right_null) {
						if(in.compareAndSet(inCopy, new DequeueSlot<>(inCopy.data, inCopy.count + 1))
								&& far.compareAndSet(farCopy, new DequeueSlot<>(data, farCopy.count + 1))) {
							this.updateRightHint(hintCopy, outNode, 1);
							return;
						}
					}
					else if(farCopy.data == right_seal) {
						if(in.compareAndSet(inCopy,  new DequeueSlot<>(inCopy.data, inCopy.count + 1))
								&& out.compareAndSet(outCopy, new DequeueSlot<>(right_null, outCopy.count + 1))) {
							this.updateRightHint(hintCopy, edgeNode, array_size - 2);
							NodeHint<T> leftHint = this.findLeftEdge(this.leftNodeHint.get());
							this.updateRightHint(leftHint, leftHint.buffer, leftHint.loc);
							// retire?
						}
					}
				} // end straddling edge
			} // end boundary or straddling edge
		} // end while
	}	// end push_right

	@SuppressWarnings("unchecked")
	public T pop_left() {
		NodeHint<T> hintCopy;
		NodeHint<T> edge;
		Node<T> edgeNode;
		int edgeIndex;
		AtomicReference<DequeueSlot<Object>> in;
		DequeueSlot<Object> inCopy;
		AtomicReference<DequeueSlot<Object>> out;
		DequeueSlot<Object> outCopy;
		int count = 0;
		while(true) {
			count++;
			if(count > 1000) {
				count = count + 0;
			}
			hintCopy = this.leftNodeHint.get();
			edge = this.findLeftEdge(hintCopy);
			edgeNode = edge.buffer;
			edgeIndex = edge.loc;

			in = edgeNode.array[edgeIndex];
			inCopy = in.get();
			out = edgeNode.array[edgeIndex - 1];
			outCopy = out.get();
			
			if((inCopy.data == left_null || inCopy.data == right_seal)
					|| (edgeIndex != 1 && outCopy.data != left_null)
					|| (edgeIndex == array_size - 1 && inCopy.data != right_null)) 
				continue;
			
			// interior edge
			// pop edge, or empty 
			if(edgeIndex != 1) {
				if(inCopy.data == right_null && in.get() == inCopy)
					return null; // empty
				if(out.compareAndSet(outCopy, new DequeueSlot<>(left_null, outCopy.count + 1))
						&& in.compareAndSet(inCopy, new DequeueSlot<>(left_null, inCopy.count + 1))) {
					this.updateLeftHint(hintCopy, edgeNode, edgeIndex + 1);
					return (T) inCopy.data;
				}
			} // end interior pop
			
			// edge is on the border between arrays, seal left node, remove left node, then pop
			else {
				// check straddle edge ? dafuq this mean
				if(outCopy.data != left_null) {
					Node<T> outNode = (Node<T>) outCopy.data;
					AtomicReference<DequeueSlot<Object>> far = outNode.array[array_size - 2];
					DequeueSlot<Object> farCopy = far.get();
					
					// check that left neighbor points back
					AtomicReference<DequeueSlot<Object>> back = outNode.array[array_size - 1];
					DequeueSlot<Object> backCopy = back.get();
					
					if((Node<T>) backCopy.data != edgeNode) continue;
					
					// check for straddle edge and seal
					if(farCopy.data == left_null) {
						if(inCopy.data == right_null || inCopy.data == right_seal && in.get() == inCopy) {
							return null;
						}
						if(in.compareAndSet(inCopy,	new DequeueSlot<>(inCopy.data, inCopy.count + 1))
								&& far.compareAndSet(farCopy, new DequeueSlot<>(left_seal, farCopy.count + 1))) {
							farCopy.data = left_seal;
							farCopy.count++;
							inCopy.count++;
						}
					}
					
					// check for sealed left node and remove
					if(farCopy.data == left_seal) {
						if(inCopy.data == right_null && in.get() == inCopy) {
							return null;
						}
						
						if(in.compareAndSet(inCopy, new DequeueSlot<>(inCopy.data, inCopy.count + 1))
								&& out.compareAndSet(outCopy, new DequeueSlot<>(left_null, outCopy.count + 1))) {
							hintCopy = this.updateLeftHint(hintCopy, edgeNode, 1);
							NodeHint<T> right = this.findRightEdge(this.rightNodeHint.get());
							this.updateRightHint(right, right.buffer, right.loc);
							// retire?
							inCopy.count++;
							outCopy.data = left_null;
							outCopy.count++;
						}
					}
				}
				if(outCopy.data == left_null) {
					if(inCopy.data == right_null && in.get() == inCopy) {
						return null;
					}
					if(out.compareAndSet(outCopy, new DequeueSlot<>(left_null, outCopy.count + 1))
							&& in.compareAndSet(inCopy, new DequeueSlot<>(left_null, inCopy.count + 1))) {
						this.updateLeftHint(hintCopy, edgeNode, 2);
						return (T) inCopy.data;
					}
				}
			} // end boundary or straddle edge
		} // end while
	} // end pop_left

	@SuppressWarnings("unchecked")
	public T pop_right() {
		NodeHint<T> hintCopy;
		NodeHint<T> edge;
		Node<T> edgeNode;
		int edgeIndex;
		AtomicReference<DequeueSlot<Object>> in;
		DequeueSlot<Object> inCopy;
		AtomicReference<DequeueSlot<Object>> out;
		DequeueSlot<Object> outCopy;
		int count = 0;
		while(true) {
			count++;
			if(count > 1000)
				count = count + 0;
			hintCopy = this.rightNodeHint.get();
			edge = this.findRightEdge(hintCopy);
			edgeNode = edge.buffer;
			edgeIndex = edge.loc;

			in = edgeNode.array[edgeIndex];
			inCopy = in.get();
			out = edgeNode.array[edgeIndex + 1];
			outCopy = out.get();
			
			if((inCopy.data == right_null || inCopy.data == left_seal)
					|| (edgeIndex != array_size - 2 && outCopy.data != right_null)
					|| (edgeIndex == 0 && inCopy.data != left_null)) 
				continue;
			
			// interior edge
			// pop edge, or empty 
			if(edgeIndex != array_size - 2) {
				if(inCopy.data == left_null && in.get() == inCopy)
					return null; // empty
				if(out.compareAndSet(outCopy, new DequeueSlot<>(right_null, outCopy.count + 1))
						&& in.compareAndSet(inCopy, new DequeueSlot<>(right_null, inCopy.count + 1))) {
					this.updateRightHint(hintCopy, edgeNode, edgeIndex - 1);
					return (T) inCopy.data;
				}
			} // end interior pop
			
			// edge is on the border between arrays, seal left node, remove left node, then pop
			else {
				// check straddle edge ? dafuq this mean
				if(outCopy.data != right_null) {
					Node<T> outNode = (Node<T>) outCopy.data;
					AtomicReference<DequeueSlot<Object>> far = outNode.array[1];
					DequeueSlot<Object> farCopy = far.get();
					
					// check that left neighbor points back
					AtomicReference<DequeueSlot<Object>> back = outNode.array[0];
					DequeueSlot<Object> backCopy = back.get();
					
					if((Node<T>) backCopy.data != edgeNode) continue;
					
					// check for straddle edge and seal
					if(farCopy.data == right_null) {
						if(inCopy.data == left_null || inCopy.data == left_seal && in.get() == inCopy) {
							return null;
						}
						if(in.compareAndSet(inCopy,	new DequeueSlot<>(inCopy.data, inCopy.count + 1))
								&& far.compareAndSet(farCopy, new DequeueSlot<>(right_seal, farCopy.count + 1))) {
							farCopy.data = right_seal;
							farCopy.count++;
							inCopy.count++;
						}
					}
					
					// check for sealed left node and remove
					if(farCopy.data == right_seal) {
						if(inCopy.data == left_null && in.get() == inCopy) {
							return null;
						}
						
						if(in.compareAndSet(inCopy, new DequeueSlot<>(inCopy.data, inCopy.count + 1))
								&& out.compareAndSet(outCopy, new DequeueSlot<>(right_null, outCopy.count + 1))) {
							hintCopy = this.updateRightHint(hintCopy, edgeNode, array_size - 2);
							NodeHint<T> left = this.findLeftEdge(this.leftNodeHint.get());
							this.updateLeftHint(left, left.buffer, left.loc);
							// retire?
							inCopy.count++;
							outCopy.data = right_null;
							outCopy.count++;
						}
					}
				}
				if(outCopy.data == right_null) {
					if(inCopy.data == left_null && in.get() == inCopy) {
						return null;
					}
					
					if(out.compareAndSet(outCopy, new DequeueSlot<>(right_null, outCopy.count + 1))
							&& in.compareAndSet(inCopy, new DequeueSlot<>(right_null, inCopy.count + 1))) {
						this.updateLeftHint(hintCopy, edgeNode, array_size - 3);
						return (T) inCopy.data;
					}
				}
			} // end boundary or straddle edge
		} // end while
	}

	/**
	 * 
	 * @author Ryan Dozier
	 *
	 * @param <T> Is used for generic data structure construction. 
	 */
	static class DequeueSlot <T> {
		public T data;
		public int count;
		/** 
		 * @param data To be stored in the data structure
		 */
		public DequeueSlot(T data) {
			this.data = data;
			this.count = 0;
		}

		public DequeueSlot(T data, int count) {
			this.data = data;
			this.count = count;
		}
	}

	static class NodeHint<T> {
		public Node<T> buffer;
		public int loc;

		public NodeHint(Node<T> node, int index) {
			this.buffer = node;
			this.loc = index;
		}
	}

	static class Node<T> {
		public int leftHint;
		public int rightHint;
		//public DequeueSlot<T>[] array;
		public AtomicReference<DequeueSlot<Object>>[] array;


		@SuppressWarnings("unchecked")
		public Node(int half) {
			//this.array = (DequeueSlot<T>[]) new DequeueSlot<?>[array_size];
			this.array = (AtomicReference<DequeueSlot<Object>>[]) new AtomicReference[array_size];
			for(int i = 0; i < half; i++) 
				array[i] = new AtomicReference<>(new DequeueSlot<>((T) left_null));
			for(int i = half; i < this.array.length; i++) 
				array[i] = new AtomicReference<>(new DequeueSlot<>((T) right_null));

			this.leftHint = half-1;
			this.rightHint = half;
		}
	}
}
