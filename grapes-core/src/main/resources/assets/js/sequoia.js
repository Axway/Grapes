function getURLParameter(name) {
    return decodeURI(
        (RegExp(name + '=' + '(.+?)(&|$)').exec(location.search)||[,null])[1]
    );
}

function loadModuleNames(){
		return $.ajax({
            type: "GET",
            accept: {
  				json: 'application/json'
			},
            url: "/module/names",
            data: {},
            dataType: "json",
            success: function(data, textStatus) {
                var html = "";
                $.each(data, function(i, name) {
    				html += "<option value=\"";
                    html += name + "\">";
                    html += name + "</option>";
				});

                $("#moduleNames").empty().append(html);
            }    
        });  
}

function loadModuleVersions(var1){
	    return $.ajax({
            type: "GET",
            accept: {
  				json: 'application/json'
			},
            url: var1,
            data: {},
            dataType: "json",
            success: function(data, textStatus) {
                var html = "";
                $.each(data, function(i, version) {
    				html += "<option value=\"";
                    html += version + "\">";
                    html += version + "</option>";
				});

                $("#moduleVersions").empty().append(html);
            }    
        });  
}

function getModuleGraph(url,chartId){
		var html = "";
         $.getJSON(url, function(data) {
			var g = new Graph();
			var graph = data;
						
		  /* modify the edge creation to attach random weights */
		  g.edgeFactory.build = function(source, target, label) {
		    var e = jQuery.extend(true, {}, this.template);
		    e.source = source;
		    e.target = target;
		    e.style.label = label;
		    return e;
		  }
		  		  
		  /* Adding all the nodes */ 
		  	$.each(graph.elements, function (index, value) {
                g.addNode(value.value, { render: getModuleGraphNodeRenderer(value)});
            });
		  
		  /* Adding all the edges */
		  $.each(graph.dependencies, function (index, value) {
		    g.addEdge(value.source , value.target, value.type, {directed: true});
		  });
		
		  /* layout the graph using the Spring layout implementation */
		  var layouter = new Graph.Layout.Spring(g);
		
		  var width = $("#" + chartId).width();
		  var height = $("#" + chartId).height();
		  $("#"+ chartId).html("");
		  var renderer = new Graph.Renderer.Raphael(chartId, g, width, height);
		
		  layouter.layout();
		  renderer.draw();
        });
}

function getModuleGraphNodeRenderer(element){
	var render = function(r, n) {
	var elSize = (element.value.length * 7.5) + 20
    frame = r.rect(n.point[0] - 30, n.point[1] - 13, 145 , 50);

    frame.attr({
        'stroke-width' : (n.distance === 0 ? '5px' : '2px')
      });
          
    if(element.root){
      frame.attr({
        'fill': '#feb'
      });
  	 } 
  	 else {
  	 	frame.attr({
        'fill': 'white'
      });
  	 };
  	 
    /* the Raphael set is obligatory, containing all you want to display */
    var set = r.set()
      .push(frame, r.text(n.point[0] + 45, n.point[1] + 10,element.value + "\n" + element.version))
      .attr({"font-size":"15px"});
    return set;
  };
  
  return render;
}

function getModuleTree(url, chartId, parentWidth, parentHeight){
var m = [20, 120, 20, 120],
    w = parentWidth - m[1] - m[3],
    h = parentHeight - m[0] - m[2],
    i = 0,
    root;

var tree = d3.layout.tree()
    .size([h, w]);

var diagonal = d3.svg.diagonal()
    .projection(function(d) { return [d.y, d.x]; });

$("#"+ chartId).html("");

var vis = d3.select("#"+ chartId).append("svg:svg")
    .attr("width", w + m[1] + m[3])
    .attr("height", h + m[0] + m[2])
  .append("svg:g")
    .attr("transform", "translate(" + m[3] + "," + m[0] + ")");

d3.json(url, function(json) {

  root = json;
  root.x0 = h / 2;
  root.y0 = 0;

  function toggleAll(d) {
    if (d.children) {
      d.children.forEach(toggleAll);
      toggle(d);
    }
  }
  update(root);
  $("#loader-indicator").hide();
});

function update(source) {
  var duration = d3.event && d3.event.altKey ? 5000 : 500;

  // Compute the new tree layout.
  var nodes = tree.nodes(root).reverse();

  // Normalize for fixed-depth.
  nodes.forEach(function(d) { d.y = d.depth * 180; });

  // Update the nodes…
  var node = vis.selectAll("g.node")
      .data(nodes, function(d) { return d.id || (d.id = ++i); });

  // Enter any new nodes at the parent's previous position.
  var nodeEnter = node.enter().append("svg:g")
      .attr("class", "node")
      .attr("transform", function(d) { return "translate(" + source.y0 + "," + source.x0 + ")"; })
      .on("click", function(d) { toggle(d); update(d); });

  nodeEnter.append("svg:circle")
      .attr("r", 1e-6)
      .style("fill", function(d) { return d._children ? "lightsteelblue" : "#fff"; });

  nodeEnter.append("svg:text")
      .attr("x", function(d) { return d.children || d._children ? -10 : 10; })
      .attr("dy", ".35em")
      .attr("text-anchor", function(d) { return d.children || d._children ? "end" : "start"; })
      .text(function(d) { return d.name; })
      .style("fill-opacity", 1e-6);

  // Transition nodes to their new position.
  var nodeUpdate = node.transition()
      .duration(duration)
      .attr("transform", function(d) { return "translate(" + d.y + "," + d.x + ")"; });

  nodeUpdate.select("circle")
      .attr("r", 4.5)
      .style("fill", function(d) { return d._children ? "lightsteelblue" : "#fff"; });

  nodeUpdate.select("text")
      .style("fill-opacity", 1);

  // Transition exiting nodes to the parent's new position.
  var nodeExit = node.exit().transition()
      .duration(duration)
      .attr("transform", function(d) { return "translate(" + source.y + "," + source.x + ")"; })
      .remove();

  nodeExit.select("circle")
      .attr("r", 1e-6);

  nodeExit.select("text")
      .style("fill-opacity", 1e-6);

  // Update the links…
  var link = vis.selectAll("path.link")
      .data(tree.links(nodes), function(d) { return d.target.id; });

  // Enter any new links at the parent's previous position.
  link.enter().insert("svg:path", "g")
      .attr("class", "link")
      .attr("d", function(d) {
        var o = {x: source.x0, y: source.y0};
        return diagonal({source: o, target: o});
      })
    .transition()
      .duration(duration)
      .attr("d", diagonal);

  // Transition links to their new position.
  link.transition()
      .duration(duration)
      .attr("d", diagonal);

  // Transition exiting nodes to the parent's new position.
  link.exit().transition()
      .duration(duration)
      .attr("d", function(d) {
        var o = {x: source.x, y: source.y};
        return diagonal({source: o, target: o});
      })
      .remove();

  // Stash the old positions for transition.
  nodes.forEach(function(d) {
    d.x0 = d.x;
    d.y0 = d.y;
  });
}

// Toggle children.
function toggle(d) {
  if (d.children) {
    d._children = d.children;
    d.children = null;
  } else {
    d.children = d._children;
    d._children = null;
  }
}
}

function getJsonGroupidTable(url, chartId){
var w = 1120,
    h = 600,
    x = d3.scale.linear().range([0, w]),
    y = d3.scale.linear().range([0, h]);

var vis = d3.select("#"+ chartId).append("div")
    .attr("class", "chart")
    .style("width", w + "px")
    .style("height", h + "px")
  .append("svg:svg")
    .attr("width", w)
    .attr("height", h);

var partition = d3.layout.partition()
    .value(function(d) { return d.size; });

d3.json(url, function(root) {
  var g = vis.selectAll("g")
      .data(partition.nodes(root))
    .enter().append("svg:g")
      .attr("transform", function(d) { return "translate(" + x(d.y) + "," + y(d.x) + ")"; })
      .on("click", click);

  var kx = w / root.dx,
      ky = h / 1;

  g.append("svg:rect")
      .attr("width", root.dy * kx)
      .attr("height", function(d) { return d.dx * ky; })
      .attr("class", function(d) { return d.children ? "parent" : "child"; });

  g.append("svg:text")
      .attr("transform", transform)
      .attr("dy", ".35em")
      .style("opacity", function(d) { return d.dx * ky > 12 ? 1 : 0; })
      .text(function(d) { return d.name; })

  d3.select(window)
      .on("click", function() { click(root); })

  function click(d) {
    if (!d.children) return;

    kx = (d.y ? w - 40 : w) / (1 - d.y);
    ky = h / d.dx;
    x.domain([d.y, 1]).range([d.y ? 40 : 0, w]);
    y.domain([d.x, d.x + d.dx]);

    var t = g.transition()
        .duration(d3.event.altKey ? 7500 : 750)
        .attr("transform", function(d) { return "translate(" + x(d.y) + "," + y(d.x) + ")"; });

    t.select("rect")
        .attr("width", d.dy * kx)
        .attr("height", function(d) { return d.dx * ky; });

    t.select("text")
        .attr("transform", transform)
        .style("opacity", function(d) { return d.dx * ky > 12 ? 1 : 0; });

    d3.event.stopPropagation();
  }

  function transform(d) {
    return "translate(8," + d.dx * ky / 2 + ")";
  }
});
}


