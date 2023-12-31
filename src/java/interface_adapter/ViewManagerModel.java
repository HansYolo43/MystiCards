package interface_adapter;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class ViewManagerModel {
        private String activeViewName;

        public String getActiveView() {
            return activeViewName;
        }
        public void setActiveView(String activeView) {
            this.activeViewName = activeView;
        }
        private final PropertyChangeSupport support = new PropertyChangeSupport(this);

        public void firePropertyChanged() {
            support.firePropertyChange("view", null, this.activeViewName);
        }

        public void addPropertyChangeListener(PropertyChangeListener listener) {
            support.addPropertyChangeListener(listener);
        }
}
