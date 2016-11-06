package de.qaware.rat.codec.docx.utils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;

import org.docx4j.TraversalUtil;
import org.docx4j.TraversalUtil.CallbackImpl;
import org.docx4j.XmlUtils;
import org.docx4j.wml.Body;
import org.docx4j.wml.CommentRangeEnd;
import org.docx4j.wml.CommentRangeStart;
import org.docx4j.wml.ContentAccessor;
import org.docx4j.wml.R.CommentReference;
import org.jvnet.jaxb2_commons.ppp.Child;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@code CommentFinder} class finds comment elements in a word document.
 * 
 * <p>
 * The class provides functionality to remove comments elements
 * (commentRangeStart, commentRangeEnd and commentReference) by a given id.
 * 
 * @author Matthias
 *
 */
public class CommentFinder extends CallbackImpl {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommentFinder.class);
    private List<Child> commentElements = new ArrayList<Child>();

    @SuppressWarnings("rawtypes")
    @Override
    public List<Object> apply(Object o) {

        if (o instanceof javax.xml.bind.JAXBElement
                && (((JAXBElement) o).getName().getLocalPart().equals("commentReference")
                        || ((JAXBElement) o).getName().getLocalPart().equals("commentRangeStart")
                        || ((JAXBElement) o).getName().getLocalPart().equals("commentRangeEnd"))) {
            commentElements.add((Child) XmlUtils.unwrap(o));
        } else if (o instanceof CommentReference || o instanceof CommentRangeStart || o instanceof CommentRangeEnd) {
            commentElements.add((Child) o);
        }
        return null;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void walkJAXBElements(Object parent) {

        List<Object> children = getChildren(parent);
        if (children != null) {

            for (Object o : children) {

                if (o instanceof javax.xml.bind.JAXBElement
                        && (((JAXBElement) o).getName().getLocalPart().equals("commentReference")
                                || ((JAXBElement) o).getName().getLocalPart().equals("commentRangeStart")
                                || ((JAXBElement) o).getName().getLocalPart().equals("commentRangeEnd"))) {

                    ((Child) ((JAXBElement) o).getValue()).setParent(XmlUtils.unwrap(parent));
                } else {
                    o = XmlUtils.unwrap(o);
                    if (o instanceof Child) {
                        ((Child) o).setParent(XmlUtils.unwrap(parent));
                    }
                }

                this.apply(o);

                if (this.shouldTraverse(o)) {
                    walkJAXBElements(o);
                }

            }
        }
    }

    /**
     * Remove all comment elements (commentRangeStart, commentRangeEnd and
     * commentReference) from a word document by a given comment id.
     * 
     * @param body
     *            the body element to remove comment elements from.
     * @param commentId
     *            the comment id to remove.
     */
    public static void deleteCommentElements(Body body, BigInteger commentId) {
        CommentFinder cf = new CommentFinder();
        new TraversalUtil(body, cf);

        for (Child commentElement : cf.commentElements) {
            int id = 0;
            if (commentElement instanceof CommentReference) {
                id = ((CommentReference) commentElement).getId().intValue();
            }

            if (commentElement instanceof CommentReference) {
                id = ((CommentReference) commentElement).getId().intValue();
            }

            if (commentElement instanceof CommentReference) {
                id = ((CommentReference) commentElement).getId().intValue();
            }

            if (id == commentId.intValue()) {
                LOGGER.debug(String.format("Removed comment element with id: '%s'.", id));
                Object parent = commentElement.getParent();
                List<Object> theList = ((ContentAccessor) parent).getContent();
                remove(theList, commentElement);
            }
        }
    }

    private static boolean remove(List<Object> theList, Object bm) {
        for (Object ox : theList) {
            if (XmlUtils.unwrap(ox).equals(bm)) {
                return theList.remove(ox);
            }
        }
        return false;
    }
}