package org.richfaces.renderkit.html;

import org.richfaces.component.AbstractContextMenu;
import org.richfaces.component.AbstractMenuGroup;
import org.richfaces.component.AbstractMenuItem;
import org.richfaces.component.AbstractMenuSeparator;
import org.richfaces.component.Mode;
import org.richfaces.component.Positioning;
import org.richfaces.renderkit.RenderKitUtils;
import org.richfaces.renderkit.RenderKitUtils.ScriptHashVariableWrapper;
import org.richfaces.renderkit.RendererBase;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ResourceDependencies({ @ResourceDependency(library = "org.richfaces", name = "ajax.reslib"),
        @ResourceDependency(library = "org.richfaces", name = "base-component.reslib"),
        @ResourceDependency(name = "jquery.position.js"), @ResourceDependency(name = "richfaces-event.js"),
        @ResourceDependency(library = "org.richfaces", name = "popup.js"),
        @ResourceDependency(library = "org.richfaces", name = "menuKeyNavigation.js"),
        @ResourceDependency(library = "org.richfaces", name = "menu-base.js"),
        @ResourceDependency(library = "org.richfaces", name = "menu.js"),
        @ResourceDependency(library = "org.richfaces", name = "contextmenu.ecss", target = "head") })
public abstract class ContextMenuRendererBase extends RendererBase {
    public static final String RENDERER_TYPE = "org.richfaces.ContextMenuRenderer";
    public static final int DEFAULT_MIN_POPUP_WIDTH = 250;
    public static final String DEFAULT_SHOWEVENT = "mouseover";

    @Override
    public void renderChildren(FacesContext facesContext, UIComponent component) throws IOException {
        AbstractContextMenu contextMenu = (AbstractContextMenu) component;

        for (UIComponent child : contextMenu.getChildren()) {
            if (child.isRendered()
                    && (child instanceof AbstractMenuGroup || child instanceof AbstractMenuItem || child instanceof AbstractMenuSeparator)) {

                child.encodeAll(facesContext);
            }
        }
    }

    protected boolean isDisabled(FacesContext facesContext, UIComponent component) {
        if (component instanceof AbstractContextMenu) {
            return ((AbstractContextMenu) component).isDisabled();
        }
        return false;
    }

    protected UIComponent getLabelFacet(FacesContext facesContext, UIComponent component) {
        UIComponent facet = null;
        AbstractContextMenu ddmenu = (AbstractContextMenu) component;
        if (ddmenu != null) {
            facet = ddmenu.getFacet(AbstractContextMenu.Facets.labelDisabled.toString());
            if (!ddmenu.isDisabled() || facet == null) {
                facet = ddmenu.getFacet(AbstractContextMenu.Facets.label.toString());
            }
        }
        return facet;
    }

    public List<Map<String, Object>> getMenuGroups(FacesContext facesContext, UIComponent component) {
        List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
        List<AbstractMenuGroup> groups = new ArrayList<AbstractMenuGroup>();
        if (component instanceof AbstractContextMenu) {
            if (component.isRendered() && !((AbstractContextMenu) component).isDisabled()) {
                getMenuGroups(component, groups);
            }
        }
        for (AbstractMenuGroup group : groups) {
            if (group.isRendered() && !group.isDisabled()) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("id", group.getClientId(facesContext));
                RenderKitUtils.addToScriptHash(map, "onhide", group.getOnhide(), null, ScriptHashVariableWrapper.eventHandler);
                RenderKitUtils.addToScriptHash(map, "onshow", group.getOnshow(), null, ScriptHashVariableWrapper.eventHandler);
                RenderKitUtils.addToScriptHash(map, "verticalOffset", group.getVerticalOffset(), "0");
                RenderKitUtils.addToScriptHash(map, "horizontalOffset", group.getHorizontalOffset(), "0");

                Positioning jointPoint = group.getJointPoint();
                if (jointPoint == null) {
                    jointPoint = Positioning.DEFAULT;
                }
                RenderKitUtils.addToScriptHash(map, "jointPoint", jointPoint.getValue(),
                        Positioning.DEFAULT.getValue());

                Positioning direction = group.getDirection();
                if (direction == null) {
                    direction = Positioning.DEFAULT;
                }
                RenderKitUtils.addToScriptHash(map, "direction", direction.getValue(),
                        Positioning.DEFAULT.getValue());

                results.add(map);
            }
        }
        return results;
    }

    protected int getPopupWidth(UIComponent component) {
        int width = ((AbstractContextMenu) component).getPopupWidth();
        if (width <= 0) {
            width = DEFAULT_MIN_POPUP_WIDTH;
        }
        return width;
    }

    protected Mode getMode(UIComponent component) {
        Mode mode = ((AbstractContextMenu) component).getMode();
        if (mode == null) {
            mode = Mode.server;
        }
        return mode;
    }

    protected Positioning getJointPoint(UIComponent component) {
        Positioning jointPoint = ((AbstractContextMenu) component).getJointPoint();
        if (jointPoint == null) {
            jointPoint = Positioning.DEFAULT;
        }
        return jointPoint;
    }

    protected Positioning getDirection(UIComponent component) {
        Positioning direction = ((AbstractContextMenu) component).getDirection();
        if (direction == null) {
            direction = Positioning.DEFAULT;
        }
        return direction;
    }

    private void getMenuGroups(UIComponent component, List<AbstractMenuGroup> list) {
        if (component != null && list != null) {
            for (UIComponent c : component.getChildren()) {
                if (c instanceof AbstractMenuGroup) {
                    list.add((AbstractMenuGroup) c);
                }
                getMenuGroups(c, list);
            }
        }
    }

    protected String getShowEvent(UIComponent component) {
        String value = ((AbstractContextMenu) component).getShowEvent();
        if (value == null || "".equals(value)) {
            value = DEFAULT_SHOWEVENT;
        }
        return value;
    }
}
