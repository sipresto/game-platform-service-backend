package be.kdg.ipj3.users.domain;

import org.jmolecules.ddd.annotation.ValueObject;

import java.util.EnumMap;
import java.util.Map;

@ValueObject
public record VisibilitySettings(Map<VisibleField, Visibility> fieldVisibilities) {
    public VisibilitySettings(Map<VisibleField, Visibility> fieldVisibilities) {
        this.fieldVisibilities = new EnumMap<>(fieldVisibilities);
    }

    public static VisibilitySettings defaultPublic() {
        Map<VisibleField, Visibility> map = new EnumMap<>(VisibleField.class);
        map.put(VisibleField.PROFILE, Visibility.PUBLIC);
        map.put(VisibleField.POINTS, Visibility.PUBLIC);
        map.put(VisibleField.FAVORITE_GAMES, Visibility.PUBLIC);
        map.put(VisibleField.OWNED_GAMES, Visibility.PUBLIC);
        map.put(VisibleField.FRIENDS_LIST, Visibility.PUBLIC);
        return new VisibilitySettings(map);
    }

    public static VisibilitySettings template(Visibility template) {
        Map<VisibleField, Visibility> map = new EnumMap<>(VisibleField.class);
        for (VisibleField field : VisibleField.values()) {
            map.put(field, template);
        }
        map.put(VisibleField.PROFILE, Visibility.PUBLIC);
        return new VisibilitySettings(map);
    }

    public VisibilitySettings withFieldVisibility(VisibleField field, Visibility visibility) {
        Map<VisibleField, Visibility> newMap = new EnumMap<>(this.fieldVisibilities);
        newMap.put(field, visibility);
        return new VisibilitySettings(newMap);
    }

    public Visibility getVisibility(VisibleField field) {
        return fieldVisibilities.get(field);
    }

    public boolean isVisibleTo(VisibleField field, User viewer, User target) {
        Visibility visibility = getVisibility(field);

        if (viewer != null && viewer.getId().equals(target.getId())) {
            return true;
        }

        return switch (visibility) {
            case PUBLIC -> true;
            case FRIENDS_ONLY -> viewer != null &&
                    target.getFriends().contains(viewer.getId());
            case PRIVATE -> false;
        };
    }

    @Override
    public Map<VisibleField, Visibility> fieldVisibilities() {
        return new EnumMap<>(fieldVisibilities);
    }
}