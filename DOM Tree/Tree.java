package structures;

import java.util.*;

/**
 * This class implements an HTML DOM Tree. Each node of the tree is a TagNode, with fields for
 * tag/text, first child and sibling.
 * 
 */
public class Tree {
	/**
	 * Root node
	 */
	TagNode root=null;
	/**
	 * Scanner used to read input HTML file when building the tree
	 */
	Scanner sc;
	/**
	 * Initializes this tree object with scanner for input HTML file
	 * 
	 * @param sc Scanner for input HTML file
	 */
	public Tree(Scanner sc) {
		this.sc = sc;
		root = null;
	}
	/**
	 * Builds the DOM tree from input HTML file, through scanner passed
	 * in to the constructor and stored in the sc field of this object. 
	 * 
	 * The root of the tree that is built is referenced by the root field of this object.
	 */
	public void build() {
		if(!sc.hasNextLine()) return;
		Stack<TagNode> tags=new Stack<TagNode>();
		root = new TagNode("html",null,null);
		tags.push(root);
		sc.nextLine();
		while(sc.hasNextLine()) {
			boolean tag=false;
			String nextline=sc.nextLine();
			if (nextline.charAt(0)=='<') {
				if (nextline.charAt(1)=='/'){
						tags.pop();
						continue;}
				else {
				nextline=nextline.replaceAll("<","");
				nextline=nextline.replaceAll(">","");
				tag=true;}
			}
			TagNode y=new TagNode(nextline,null,null);
			if (tags.peek().firstChild==null) {
				tags.peek().firstChild=y;}
			else {
				TagNode ptr=tags.peek().firstChild;
				while(ptr.sibling!=null) {
					ptr=ptr.sibling;}
				ptr.sibling=y;
			}
			if(tag) {
				tags.push(y);}
		}
	}
	/**
	 * Replaces all occurrences of an old tag in the DOM tree with a new tag
	 * @param oldTag Old tag
	 * @param newTag Replacement tag
	 */
	public void replaceTag(String oldTag, String newTag) {
		replaceTag2(root,oldTag,newTag);
	}
	private void replaceTag2(TagNode root2, String oldTag2,String newTag2) {
		if (root2==null) return;
		if (root2.tag.equals(oldTag2))
				root2.tag=newTag2;
		replaceTag2(root2.firstChild,oldTag2,newTag2);
		replaceTag2(root2.sibling,oldTag2,newTag2);
	}
	/**
	 * Boldfaces every column of the given row of the table in the DOM tree. The boldface (b)
	 * tag appears directly under the td tag of every column of this row.
	 * @param row Row to bold, first row is numbered 1 (not 0).
	 */
	public void boldRow(int row) {
		boldRow2(root,row);
		return ;
	}
	private void boldRow2(TagNode root2, int row2) {
		if (root2==null) return;
		int rownum=0;
		if (root2.tag.equals("table")) {
			root2=root2.firstChild;
			while (root2!=null) {
				rownum++;
				if (rownum==row2) {
					TagNode ptr=root2.firstChild;
					while (ptr!=null) {
						TagNode newnode=new TagNode("b",ptr.firstChild,null);
						ptr.firstChild=newnode;
						ptr=ptr.sibling;}
					break;}
				root2=root2.sibling;}
		}
		boldRow2(root2.firstChild,row2);
		boldRow2(root2.sibling,row2);
	}
	/**
	 * Remove all occurrences of a tag from the DOM tree. If the tag is p, em, or b, all occurrences of the tag
	 * are removed. If the tag is ol or ul, then All occurrences of such a tag are removed from the tree, and, 
	 * in addition, all the li tags immediately under the removed tag are converted to p tags. 
	 * @param tag Tag to be removed, can be p, em, b, ol, or ul
	 */
	public void removeTag(String tag){
		int num=howmany(root,tag);
		if(tag.equals("b")||tag.equals("p")||tag.equals("em")||tag.equals("ul")||tag.equals("ol")) {
			for(int i=1;i<=num;i++){
			removeTag1(root.firstChild,root,tag);}
		}
	}
	private int howmany(TagNode root,String tag) {
		if(root == null)return 0;
		if(!root.tag.equals(tag)) {
			return 0+howmany(root.firstChild,tag)+howmany(root.sibling,tag);}
		else {
			return 1+howmany(root.firstChild,tag)+howmany(root.sibling,tag);}
	}
	private void removeTag1(TagNode root,TagNode par,String tag) {
	if(root==null) return;
	if(par==null) return;
	if(root.tag.equals(tag)) {
		if(root.tag.equals("ul")||root.tag.equals("ol")) {
			TagNode ptr1=root.firstChild;
			while(ptr1!=null) {
				if(ptr1.tag.equals("li")) {
					ptr1.tag="p";}
				ptr1=ptr1.sibling;}
		}
		if(root==par.firstChild) {
			par.firstChild=root.firstChild;
			TagNode ptr2=root.firstChild;
			while(ptr2.sibling!=null) {
				ptr2=ptr2.sibling;
			}
			ptr2.sibling=root.sibling;
		}else if(root==par.sibling){
			TagNode ptr3=root.firstChild;
			while(ptr3.sibling!=null) {
				ptr3=ptr3.sibling;
			}
			ptr3.sibling=root.sibling;
			par.sibling=root.firstChild;
		}return;
	}
	par=root;
	removeTag1(root.firstChild,par,tag);
	removeTag1(root.sibling,par,tag);
}
	/**
	 * Adds a tag around all occurrences of a word in the DOM tree.
	 * 
	 * @param word Word around which tag is to be added
	 * @param tag Tag to be added
	 */
public void addTag(String word, String tag) {
		addTag2(null, root, word, tag);
	}
	
	private void addTag2(TagNode prev, TagNode curr, String word2, String tag2) {
		// Base Case
		if (curr == null||tag2==null||tag2=="")
			return;
		if(prev != null && prev.tag.equals(tag2)){
			return;
		}
		if (tag2.equals("em") || tag2.equals("b") ) {
			boolean path=false;
			String copy=curr.tag;
			for (int index = copy.indexOf(word2);index!=-1;index = copy.indexOf(word2, index + word2.length())){
		    if (index==-1)break;
			String s1=copy.substring(0,index);
		    char c=copy.charAt(index+word2.length());
		    int end=index+word2.length();
		    if (c==','||c=='.'||c==';'||c==':'||c=='!'||c=='?') {
		    	path=true;
		    	end=end+1;
		    }
		    String s2=copy.substring(index,end);
		    if (!path) {String s3=copy.substring(end);
		    copy=s1+"@"+s2+"@"+s3;}
		    else {
		    	copy=s1+"@"+s2+"@";
		    }
		}
			System.out.println(copy);
			String[] array = copy.split("@");
			for (int i=0;i<array.length;i++) {
				System.out.print(array[i]);
			}
			//String[] array = curr.tag.split(" ");			
			int len = array.length;
			String before = "";
			String target = "";
			String after = "";
			TagNode curr2 = curr.sibling;
			
			if (len == 1) {
				TagNode temp = new TagNode(tag2, null, null);
				for (int i = 0; i < len; i++) {
					if (specCheck(array[i], word2)) {
						if (prev.firstChild == curr) {
							if (curr.sibling != null) {
								prev.firstChild = temp;
								temp.firstChild = curr;
								temp.sibling = curr.sibling;
								curr.sibling = null;
							}
							else {
								prev.firstChild = temp;
								temp.firstChild = curr;
							}
						}
						if (prev.sibling == curr) {
							if (curr.sibling != null) {
								prev.sibling = temp;
								temp.firstChild = curr;
								temp.sibling = curr.sibling;
								curr.sibling = null;
							}
							else {
								prev.sibling = temp;
								temp.firstChild = curr;
							}
						}
					}
				}	
			}
			else {
					TagNode head = null;
					TagNode tail = null;
					boolean beforeCheck = true;
					boolean targetCheck = true;
					boolean afterCheck = true;
					
					//WHILE LOOP STARTS HERE
					
					while (afterCheck == true) {
						beforeCheck = true;
						targetCheck = true;
						TagNode beforeTN = new TagNode(null, null, null);
						TagNode targetTN = new TagNode(null, null, null);
						TagNode afterTN = new TagNode(null, null, null);
						TagNode temp = new TagNode(tag2, null, null);
						before = "";
						target = "";
						after = "";
						for (int n = 0; n < len && (targetCheck == true); n++) {
							if (specCheck(array[n], word2)) {
								beforeCheck = false;
								targetCheck = false;
								target = array[n];
								targetTN.tag = target;
								if (n != len - 1) {
									for (int m = n + 1; m < len; m++) {
										after = after + array[m] + " ";
									}
									afterTN.tag = after;
									break;
								}
							}
							else if (beforeCheck == true) {
								before = before + array[n] + " ";
								beforeTN.tag = before;
							}
						}
						// no match
						if (targetCheck){
							if (prev.firstChild == curr) {
								prev.firstChild=beforeTN;
								if (head!=null) {
									prev.firstChild = head;
								}
							}
							if (prev.sibling == curr)
								prev.sibling =head;// beforeTN;
							break;
						}
						if (beforeTN.tag != null && targetTN.tag != null && afterTN.tag != null) {
							beforeTN.sibling = temp;
							temp.firstChild = targetTN;
							temp.sibling = afterTN;
						}
						else if (beforeTN.tag != null && targetTN.tag != null) {
							beforeTN.sibling = temp;
							temp.firstChild = targetTN;
						}
						else if (afterTN.tag != null) {
							temp.firstChild = targetTN;
							temp.sibling = afterTN;
						}
						
						if (head == null && beforeTN.tag != null) {
							head = beforeTN;
							tail=temp;
							}
						else if (head == null && beforeTN.tag == null) {
							temp.firstChild = targetTN;
							head = temp;
							tail=temp;
						}
						else {
							if (beforeTN.tag!=null) {
							tail.sibling = beforeTN;
							tail = temp;}
							else {
								temp.firstChild=targetTN;
								tail.sibling=temp;
								tail = temp;
							}
						}
						
						if (afterTN.tag != null) {
							String copy2=afterTN.tag;
							boolean patch=false;
							for (int index = copy2.indexOf(word2);index!=-1;index = copy2.indexOf(word2, index + word2.length())){
						    if (index==-1)break;
							String s1=copy2.substring(0,index);
						    char c=copy2.charAt(index+word2.length());
						    int end=index+word2.length();
						    if (c==','||c=='.'||c==';'||c==':'||c=='!'||c=='?') {
						    	patch=true;
						    	end=end+1;
						    }
						    String s2=copy2.substring(index,end);
						    if (!patch) {String s3=copy2.substring(end);
						    copy2=s1+"@"+s2+"@"+s3;}
						    else {
						    	copy2=s1+"@"+s2+"@";
						    }
						}
							array = copy2.split("@");
							len = array.length;
						}
						else {
							afterCheck = false;
							temp.sibling = curr2;
						}
	
					} //end of while loop		

					if (prev.firstChild == curr)  {
						prev.firstChild = head;}
					else if (prev.sibling == curr) {
						prev.sibling = head;}
					}	
			addTag2(curr, curr.firstChild, word2, tag2);
			addTag2(curr, curr.sibling,  word2, tag2);
		}
	}
	// equality check
	private boolean specCheck (String currword, String targetword) {
		String currword2 = currword.toLowerCase();
		String targetword2 = targetword.toLowerCase();
		if(currword2.equals(targetword2))
			return true;
		// Check if last character is a punctuation
		System.out.println("currword: "+currword);
		char x= currword.charAt(currword.length() - 1);
		// Check if the last character is a letter (ie to prevent addTag to tagging cows while searching word: cow)
		if (Character.isLetter(x))
			return false;
		boolean check=false;
		if (x=='!'||x==':'||x==','||x=='.'||x=='?'||x==';') {
			check=true;}
		// Remove the last character of the current target word, if the words match, return true. if not, return false
		if (targetword2.equals(currword2.substring(0, currword.length() - 1))&&check)
			return true;
		else
			return false;
	}

	/*public void addTag(String word, String tag) {
		addTag2(null, root, word, tag);
	}
	
	private void addTag2(TagNode prev, TagNode curr, String word2, String tag2) {
		// Base Case
		if (curr == null)
			return;
		// Prevents nesting of two of the same tags
		if(prev != null && prev.tag.equals(tag2)){
			return;
		}
		int count=0;
		if (tag2.equals("em") || tag2.equals("b") ) {
			String[] array = curr.tag.split(" ");
			int len = array.length;
			System.out.println(len);
			String before = "";
			String target = "";
			String after = "";
			TagNode temp = new TagNode(tag2, null, null);
			// Check if there is only 1 word
			if (len == 1) {
				for (int i = 0; i < len; i++) {
					if (specCheck(array[i], word2)) {
						if (prev.firstChild == curr) {
							if (curr.sibling != null) {
								System.out.println("hey1");
								prev.firstChild = temp;
								temp.firstChild = curr;
								temp.sibling = curr.sibling;
								curr.sibling = null;
							}
							else {
								System.out.println("hey2");
								prev.firstChild = temp;
								temp.firstChild = curr;
							}
						}
						if (prev.sibling == curr) {
							if (curr.sibling != null) {
								System.out.println("hey3");
								prev.sibling = temp;
								temp.firstChild = curr;
								temp.sibling = curr.sibling;
								curr.sibling = null;
							}
							else {
								System.out.println("hey4");
								prev.sibling = temp;
								temp.firstChild = curr;
							}
						}
					}
				}	
			}
			else {
					TagNode curr2=temp;
					TagNode head = null;
					TagNode tail = null;
					boolean beforeCheck = true;
					boolean targetCheck = true;
					boolean afterCheck = true;
					while (afterCheck == true) {
						count++;
						TagNode beforeTN = new TagNode(null, null, null);
						TagNode targetTN = new TagNode(null, null, null);
						TagNode afterTN = new TagNode(null, null, null);
						before = "";
						target = "";
						after = "";
						for (int n = 0; n < len && (targetCheck == true); n++) {
							if (specCheck(array[n], word2)) {
								beforeCheck = false;
								targetCheck = false;
								target = array[n];
								targetTN.tag = target;
								if (n != len - 1) {
								for (int m = n + 1; m < len; m++) {
									after = after + array[m] + " ";
								}
								afterTN.tag = after;
								}
							}
							else if (beforeCheck == true) {
								before = before + array[n] + " ";
								beforeTN.tag = before;
							}
						}
						// no match
						if (targetCheck){
							if (prev.firstChild == curr)
								prev.firstChild = beforeTN;
							if (prev.sibling == curr)
								prev.sibling = beforeTN;
							break;
						}
						if (beforeTN.tag != null && targetTN.tag != null && afterTN.tag != null) {
							beforeTN.sibling = temp;
							temp.firstChild = targetTN;
							temp.sibling = afterTN;
						}
						else if (beforeTN.tag!=null&& targetTN.tag != null&& count>1) {
							TagNode temp2= new TagNode(tag2, null, null);
							beforeTN.sibling = temp2;
							temp2.firstChild = targetTN;
						}
						else if (beforeTN.tag != null && targetTN.tag != null) {
							beforeTN.sibling = temp;
							temp.firstChild = targetTN;
						}
						else if (afterTN.tag != null) {
							temp.firstChild = targetTN;
							temp.sibling = afterTN;
							//addTag2(prev, afterTN,  word2, tag2);
						}
						System.out.println(temp);
						if (count>1) {
						temp.sibling=beforeTN;}
						System.out.println(curr2);
						if (count>1) {
							System.out.print("shloop "+ beforeTN);
							curr2.sibling=beforeTN;
							//tail=beforeTN;
							System.out.println("curr2:"+curr2+" "+curr2.sibling);
							System.out.println("b4TN:"+beforeTN);
						}
						if (head == null && beforeTN.tag != null) {
							head = beforeTN;
							tail=temp;					
							}
						else if (head == null && beforeTN.tag == null) {
							temp.firstChild = targetTN;
							head = temp;
							tail=temp;//
						}
						else {
							tail = targetTN;}
						if (afterTN.tag != null) {
							//System.out.println("hellur");
							//afterCheck =true;
							targetCheck = true;
							array = afterTN.tag.split(" ");
							len = array.length;
							if (head == null && beforeTN.tag != null) {
								head = beforeTN;
								tail=temp;}//
							else if (head == null && beforeTN.tag == null) {
								temp.firstChild = targetTN;
								head = temp;
								tail=temp;//
							}
							else {
								//System.out.println("tail2: "+tail);
								tail=afterTN;
								//tail = temp;//
								//System.out.println("tail2b: "+tail);
								}
						}
						else {
							afterCheck = false;
						}
						//System.out.println("tail2cc: "+tail);
						/*adding spaces but have to do right amt
						if (!targetCheck) {
							beforeTN.tag = before+" ";
							if(afterTN.tag!=null) {
								afterTN.tag = " "+after;
							}
						//}
						//afterTN2=afterTN;

						//System.out.println("tail2c: "+tail);
					}//while loop ends here
					//System.out.println("tail2d: "+tail);
					if (prev.firstChild == curr) {
						prev.firstChild = head;}
						//curr=head;}
					else if (prev.sibling == curr) {
						prev.sibling = head;}
					if(curr.sibling != null) {
						tail.sibling = curr.sibling;}
						//curr.sibling = null;}
				}
			}
		
		addTag2(curr, curr.firstChild, word2, tag2);
		addTag2(curr, curr.sibling,  word2, tag2);
	}
	// equality check
	private boolean specCheck (String currword, String targetword) {
		String currword2 = currword.toLowerCase();
		String targetword2 = targetword.toLowerCase();
		if(currword2.equals(targetword2))
			return true;
		// Check if last character is a punctuation
		char x= currword.charAt(currword.length() - 1);
		// Check if the last character is a letter (ie to prevent addTag to tagging cows while searching word: cow)
		if (Character.isLetter(x))
			return false;
		boolean check=false;
		if (x=='!'||x==':'||x==','||x=='.'||x=='?'||x==';') {
			check=true;}
		// Remove the last character of the current target word, if the words match, return true. if not, return false
		if (targetword2.equals(currword2.substring(0, currword.length() - 1))&&check)
			return true;
		else
			return false;
	}*/
	/**
	 * Gets the HTML represented by this DOM tree. The returned string includes
	 * new lines, so that when it is printed, it will be identical to the
	 * input file from which the DOM tree was built.
	 * 
	 * @return HTML string, including new lines. 
	 */
	public String getHTML() {
		StringBuilder sb = new StringBuilder();
		getHTML(root, sb);
		return sb.toString();
	}
	private void getHTML(TagNode root, StringBuilder sb) {
		for (TagNode ptr=root; ptr != null;ptr=ptr.sibling) {
			if (ptr.firstChild == null) {
				sb.append(ptr.tag);
				sb.append("\n");
			} else {
				sb.append("<");
				sb.append(ptr.tag);
				sb.append(">\n");
				getHTML(ptr.firstChild, sb);
				sb.append("</");
				sb.append(ptr.tag);
				sb.append(">\n");}
		}
	}
	/*** Prints the DOM tree. */
	public void print() {
		print(root, 1);}
	private void print(TagNode root, int level) {
		for (TagNode ptr=root; ptr != null;ptr=ptr.sibling) {
			for (int i=0; i < level-1; i++) {
				System.out.print("      ");
			};
			if (root != this.root) {
				System.out.print("|----");
			} else {
				System.out.print("     ");
			}
			System.out.println(ptr.tag);
			if (ptr.firstChild != null) {
				print(ptr.firstChild, level+1);
			}
		}
	}
}