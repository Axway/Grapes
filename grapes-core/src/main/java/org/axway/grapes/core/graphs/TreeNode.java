package org.axway.grapes.core.graphs;

import java.util.ArrayList;
import java.util.List;

public class TreeNode {

	private String name;
	private List<TreeNode> children = new ArrayList<TreeNode>();

	public void setName(final String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}

	public void setChildren(final List<TreeNode> children) {
		this.children = children;
	}
	public List<TreeNode> getChildren() {
		return children;
	}

	public void addChild(final TreeNode subTree) {
		children.add(subTree);		
	}

}
