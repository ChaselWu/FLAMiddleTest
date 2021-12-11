window.onload = function () {
// 实例化 Minimap 插件
    const minimap = new G6.Minimap({
        size: [100, 100],
        className: "minimap",
        type: 'delegate'
    });

// 实例化 Grid 插件
    const grid = new G6.Grid();

    const graph = new G6.Graph({
        container: 'mountNode',
        width: 1400,
        height: 800,
        defaultNode: {
            labelCfg: {
                style: {
                    fill: '#fff'
                }
            }
        },
        defaultEdge: {
            //type:'loop',
            type: 'quadratic', // 指定边的形状为二阶贝塞尔曲线
            style: {
                stroke: '#e2e2e2',
            },
            labelCfg: {
                autoRotate: true
            }
        },
        nodeStateStyles: {
            hover: {
                fill: 'lightsteelblue'
            },
            click: {
                stroke: '#000',
                lineWidth: 3
            }
        },
        edgeStateStyles: {
            click: {
                stroke: 'steelblue'
            }
        },
        layout: {
            type: 'force',
            linkDistance: 100,
            preventOverlap: true,
            nodeStrength: -30,
            edgeStrength: 0.1
        },
        modes: {
            default: ['drag-node', 'drag-canvas', 'zoom-canvas',
                // 点提示框交互工具的配置
                {
                    type: 'tooltip',
                    formatText(model) {
                        const text = 'status: ' + model.label;
                        return text;
                    },
                    shouldUpdate: e => {
                        return true;
                    }
                },
                // 边提示框交互工具的配置
                {
                    type: 'edge-tooltip',
                    formatText(model) {
                        const text = 'from: q' + model.source
                            + '<br/> to: q' + model.target
                            + '<br/> input: ' + model.label;
                        return text;
                    },
                    shouldUpdate: e => {
                        return true;
                    }
                }
            ]
        },
        plugins: [minimap, grid],    // 将 Minimap 和 Grid 插件的实例配置到图上
        fitCenter: true,
    });

    const main = async () => {
        const response = await fetch('dfa.json');
        //const response = await fetch('http://localhost:6262/dfa.json');
        const data = await response.json();
        console.log(data);
        const nodes = data.nodes;
        const edges = data.edges;
        nodes.forEach(node => {
            if (!node.style) {
                node.style = {};
            }

            node.style.lineWidth = 1;
            node.style.stroke = '#666';
            node.style.fill = 'steelblue';
            if(node.isOver){
                node.style.fill='#666';
            }
            node.type = 'circle';
            node.size = 30;
        });
        edges.forEach(edge => {
            if (!edge.style) {
                edge.style = {};
            }

            edge.style = {};
            if (edge.source == edge.target) {
                edge.type = 'loop';
                edge.loopCfg = {
                    position: edge.label == '1' ? 'bottom' : 'top',
                    dist: 100,
                    clockwise: false,
                };
            }

            edge.style.lineWidth = 1;
            edge.style.opacity = 0.6;
            edge.style.stroke = 'grey';
            if (!edge.isRepeated) {
                edge.style.endArrow = true;
            } else {
                const temp=edge.source;
                edge.source=edge.target;
                edge.target=temp;
                edge.style.startArrow=true;
                edge.curveOffset=-50;
            }
        });


        graph.data(data);
        graph.render();

        graph.on('node:mouseenter', e => {
            const nodeItem = e.item;
            graph.setItemState(nodeItem, 'hover', true);
        });
        graph.on('node:mouseleave', e => {
            const nodeItem = e.item;
            graph.setItemState(nodeItem, 'hover', false);
        });
        graph.on('node:click', e => {
            const clickNodes = graph.findAllByState('node', 'click');
            clickNodes.forEach(cn => {
                graph.setItemState(cn, 'click', false);
            });
            const nodeItem = e.item;
            graph.setItemState(nodeItem, 'click', true);
        });
        graph.on('edge:click', e => {
            const clickEdges = graph.findAllByState('edge', 'click');
            clickEdges.forEach(ce => {
                graph.setItemState(ce, 'click', false);
            });
            const edgeItem = e.item;
            graph.setItemState(edgeItem, 'click', true);
        });
    };
    main();
}