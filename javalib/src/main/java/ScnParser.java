import java.util.ArrayList;
import java.util.LinkedList;
import java.util.regex.Pattern;

public class ScnParser {
    /**开始节点*/
    private static final char START_NODE = '{';
    /**结束节点*/
    private static final char END_NODE = '}';

    private final String scnContent;
    private final char[] chars;
//    private final LinkedList<HtmlNode> nodesTemp = new LinkedList<>();
//    private final ArrayList<HtmlNode> parserNode = new ArrayList<>();
    private int vernier = 0;

    public ScnParser(String scnContent) {
        this.scnContent = scnContent;
        chars = scnContent.toCharArray();
    }

    public ScnParser parser() {
        vernier = 0;
//        nodesTemp.clear();
        int startIndex = -1;
        int endIndex = -1;
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            switch (c) {
                case START_NODE:
                    startIndex = i;
                    break;
                case END_NODE:
                    endIndex = i;
                    break;
            }
            if (startIndex >= 0 && endIndex > 0 && endIndex > startIndex) {
                String str = scnContent.substring(startIndex, endIndex + 1);
//                if (Pattern.matches(nodeRegex, str)) {
//                    符合正则
//                    addNode(startIndex, str);
//                }
                vernier = endIndex + 1;
                startIndex = -1;
                endIndex = -1;
            }
        }
        if (vernier == 0) {
//            HtmlNode node = new HtmlNode();
//            node.setTextNode(0, scnContent);
//            parserNode.add(node);
        }
//        nodesTemp.clear();
        return this;
    }

//    public ArrayList<HtmlNode> getNodes() {
//        return parserNode;
//    }
//
//    private void addNode(final int index, final String str) {
//        if (index > vernier && nodesTemp.size() == 0) {
//            String substring = scnContent.substring(vernier, index);
//            if (substring != null) {
//                HtmlNode node = new HtmlNode();
//                node.setTextNode(vernier, substring);
//                parserNode.add(node);
//            }
//        }
//        if (Pattern.matches(SINGLE_NODE, str) || Pattern.matches(SINGLE_NODE2, str)) {
//            //换行标签
//            if (nodesTemp.size() > 0) {
//                HtmlNode lastNode = nodesTemp.getLast();
//                if (!lastNode.isEnd() && !lastNode.isHasChild()) {
//                    int start = lastNode.getStartIndex() + lastNode.getStartNode().length();
//                    if (start < index) {
//                        String content = scnContent.substring(start, index);
//                        HtmlNode node = new HtmlNode();
//                        node.setTextNode(start, content);
//                        lastNode.addChildNode(node);
//                    }
//                }
//
//                HtmlNode node = new HtmlNode();
//                node.setIndepNode(index, str);
//                nodesTemp.getLast().addChildNode(node);
//            } else {
//                HtmlNode node = new HtmlNode();
//                node.setIndepNode(index, str);
//                parserNode.add(node);
//            }
//        } else {
//            if (str.startsWith(START_END)) {
//                //结束标签
//                HtmlNode lastNode = nodesTemp.getLast();
//                if (lastNode.isSameNodeName(str)) {
//                    //设置结尾标签
//                    lastNode.setEnd(index, str);
//                    int start = lastNode.getStartIndex() + lastNode.getStartNode().length();
//                    String content = scnContent.substring(start, index);
//                    //设置内容
//                    lastNode.setHtml(content);
//                    //在临时列表中移除
//                    if (nodesTemp.size() == 1) {
//                        nodesTemp.removeLast();
//                        parserNode.add(lastNode);
//                    } else {
//                        nodesTemp.removeLast();
//                        HtmlNode last = nodesTemp.getLast();
//                        last.addChildNode(lastNode);
//                    }
//                } else {
//                    //当做独立标签
//                    HtmlNode node = new HtmlNode();
//                    node.setIndepNode(index, str);
//                    lastNode.addChildNode(node);
//                }
//            } else {
//                if (str.endsWith(END_END)) {
//                    if (nodesTemp.size() > 0) {
//                        HtmlNode lastNode = nodesTemp.getLast();
//                        HtmlNode node = new HtmlNode();
//                        node.setIndepNode(index, str);
//                        lastNode.addChildNode(node);
//                    } else {
//                        HtmlNode node = new HtmlNode();
//                        node.setIndepNode(index, str);
//                        nodesTemp.add(node);
//                    }
//                } else {
//                    if (nodesTemp.size() > 0) {
//                        HtmlNode lastNode = nodesTemp.getLast();
//                        if (!lastNode.isEnd() && !lastNode.isHasChild()) {
//                            int start = lastNode.getStartIndex() + lastNode.getStartNode().length();
//                            if (start < index) {
//                                String content = scnContent.substring(start, index);
//                                HtmlNode node = new HtmlNode();
//                                node.setTextNode(start, content);
//                                lastNode.addChildNode(node);
//                            }
//                        }
//                    }
//                    HtmlNode node = new HtmlNode();
//                    node.setStart(index, str);
//                    nodesTemp.add(node);
//                }
//            }
//        }
//    }

}
