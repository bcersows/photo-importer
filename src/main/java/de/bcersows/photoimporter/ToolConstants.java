package de.bcersows.photoimporter;

/**
 * Constants for the tool.
 * 
 * @author BCE
 */
public class ToolConstants {
    /** The CSS class to mark a node as a Font Awesome one. **/
    public static final String CSS_CLASS_FONT_AWESOME = "fontAwesome";

    /** FontAwesome icons. **/
    public enum ICONS {
        /** Copy icon. **/
        FA_COPY("\uf0c5"),
        /** Tasks icon. **/
        FA_TASKS("\uf0ae"),
        /** Repeat icon. **/
        FA_REPEAT("\uf01e"),
        /** Exit/logout icon. **/
        FA_EXIT("\uf2f5"),

        /** Sort alphabetically ascending. **/
        FA_SORT_ASC("\uf15d"),
        /** Sort alphabetically descending. **/
        FA_SORT_DESC("\uf15e"),;

        public final String code;

        private ICONS(final String code) {
            this.code = code;
        }
    }
}
