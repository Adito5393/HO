package module.teamAnalyzer.ui;

import core.gui.theme.ImageUtilities;
import core.model.HOVerwaltung;
import core.module.config.ModuleConfig;
import core.util.Helper;
import module.teamAnalyzer.SystemManager;
import module.teamAnalyzer.ui.model.UiRatingTableModel;
import module.teamAnalyzer.ui.renderer.RatingTableCellRenderer;
import module.teamAnalyzer.vo.TeamLineup;
import java.awt.BorderLayout;
import java.util.Arrays;
import java.util.Vector;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;

public class RatingPanel extends JPanel {
	private static final long serialVersionUID = -1086256822169689318L;

	//~ Instance fields ----------------------------------------------------------------------------

	private JTable table;
    private UiRatingTableModel tableModel;
    private String[] columns = {
    		HOVerwaltung.instance().getLanguageString("RatingPanel.Area"),
    		HOVerwaltung.instance().getLanguageString("Rating"),
    		HOVerwaltung.instance().getLanguageString("Differenz_kurz"),
    		HOVerwaltung.instance().getLanguageString("RatingPanel.Relative")
                               };

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new RatingPanel object.
     */
    public RatingPanel() {
        jbInit();
    }

    //~ Methods ------------------------------------------------------------------------------------
    public void reload(TeamLineup lineup) {
        tableModel = new UiRatingTableModel(new Vector<Vector>(), new Vector<String>(Arrays.asList(columns)));
        table.setModel(tableModel);

        if ((lineup == null) || (!ModuleConfig.instance().getBoolean(SystemManager.ISLINEUP))) {
            return;
        }

        TeamLineupData myTeam = SystemManager.getPlugin().getMainPanel().getMyTeamLineupPanel();
        TeamLineupData opponentTeam = SystemManager.getPlugin().getMainPanel()
                                                   .getOpponentTeamLineupPanel();

        tableModel.addRow(getRow(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.midfield"),
                                 myTeam.getMidfield(), opponentTeam.getMidfield()));
        tableModel.addRow(getRow(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.rightdefence"),
                                 myTeam.getRightDefence(), opponentTeam.getLeftAttack()));
        tableModel.addRow(getRow(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.centraldefence"),
                                 myTeam.getMiddleDefence(), opponentTeam.getMiddleAttack()));
        tableModel.addRow(getRow(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.leftdefence"),
                                 myTeam.getLeftDefence(), opponentTeam.getRightAttack()));
        tableModel.addRow(getRow(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.rightattack"),
                                 myTeam.getRightAttack(), opponentTeam.getLeftDefence()));
        tableModel.addRow(getRow(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.centralattack"),
                                 myTeam.getMiddleAttack(), opponentTeam.getMiddleDefence()));
        tableModel.addRow(getRow(HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.leftattack"),
                                 myTeam.getLeftAttack(), opponentTeam.getRightDefence()));

        table.getTableHeader().getColumnModel().getColumn(0).setWidth(130);
        table.getTableHeader().getColumnModel().getColumn(0).setPreferredWidth(130);
        table.getTableHeader().getColumnModel().getColumn(1).setWidth(100);
        table.getTableHeader().getColumnModel().getColumn(1).setPreferredWidth(100);
    }

    private String getRating(int rating) {
        return RatingUtil.getRating(rating, ModuleConfig.instance().getBoolean(SystemManager.ISNUMERICRATING),
        		ModuleConfig.instance().getBoolean(SystemManager.ISDESCRIPTIONRATING));
    }

    private Vector<Object> getRow(String label, double myRating, double opponentRating) {
        Vector<Object> rowData = new Vector<Object>();

        rowData.add(label);
        rowData.add("" + getRating((int) myRating));

        int diff = (int) myRating - (int) opponentRating;

        double relativeVal;
        if (myRating != 0 || opponentRating != 0)
        	relativeVal = Helper.round(myRating/(myRating+opponentRating),2);
        else
        	relativeVal = 0;

//        System.out.println ("mR="+myRating+", oR="+opponentRating+", rV="+relativeVal);
        String relValString = (int)(relativeVal * 100) + "%";

        // Add a character indicating more or less than 50%
        // will be used in RatingTableCellRenderer to set the foreground color
        if (relativeVal > 0.5)
        	relValString = "+" + relValString;
        else if (relativeVal < 0.5)
        	relValString = "-" + relValString;

        // Add difference as icon
        rowData.add(  ImageUtilities.getImageIcon4Veraenderung(diff,true));
        // Add relative difference [%]
        rowData.add(relValString);

        return rowData;
    }

    private void jbInit() {
        Vector<Vector> data = new Vector<Vector>();

        tableModel = new UiRatingTableModel(data, new Vector<String>(Arrays.asList(columns)));
        table = new JTable(tableModel);

        table.setDefaultRenderer(Object.class, new RatingTableCellRenderer());

        setLayout(new BorderLayout());

        JScrollPane scrollPane = new JScrollPane(table);

        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        add(scrollPane);
    }
}
