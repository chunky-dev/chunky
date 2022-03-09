package se.llbit.chunky.ui;

/**
 * Icons made for Chunky and released under CC0
 *
 * Icons are provided as SVG Paths
 */
public class Icons {
  public final static String PORTRAIT_TO_LANDSCAPE =
    "M0-8v9h-3v3h-5v-12Zm-7 1v10h3v-3h3v-7Z" + // portrait rectangle with missing edge
    "M8 0v8h-12v-8Zm-11 1v6h10v-6Z" + // landscape rectangle
    "M1-7h2c2 0 3 1 3 3v.5h2l-3 3-3-3h2v-.5c0-1-1-1-1-1h-2Z"; // arrow

  public final static String SQUARE_DIAGONALLY_CROSSED =
    "M-6-6h11l1-1 1 1-1 1v11h-11l-1 1-1-1 1-1ZM4-5h-9v9Zm1 1-9 9h9Z";

  /**
   * base for open and closed chains icon
   * use in concatenation with {@link #CHAIN_CONNECTION_BROKEN} or {@link #CHAIN_CONNECTION_CLOSED}
   */
  public final static String CHAIN_LINK_BASE =
    "M-2-16c-1 0-2 1-2 2l0 8c0 1 1 2 2 2l4 0c1 0 2-1 2-2l0-8c0-1-1-2-2-2z" + // upper chain link outside
      "m3 2c1 0 1 1 1 1l0 6c0 1-1 1-1 1l-2 0c-1 0-1-1-1-1l0-6c0-1 1-1 1-1z" + // upper chain link inside
      "M-2 4c-1 0-2 1-2 2l0 8c0 1 1 2 2 2l4 0c1 0 2-1 2-2l0-8c0-1-1-2-2-2z" + // lower chain link outside
      "m3 2c1 0 1 1 1 1l0 6c0 1-1 1-1 1l-2 0c-1 0-1-1-1-1l0-6c0-1 1-1 1-1z"; // lower chain link inside

  /**
   * append this to the {@link #CHAIN_LINK_BASE} to create the open chains icon
   */
  public final static String CHAIN_CONNECTION_BROKEN =
    "M-1-3l2 1 0-5c0-1-1-1-1-1-1 0-1 1-1 1z" + // broken upper chain link
    "M-1 2l0 5c0 1 1 1 1 1 1 0 1-1 1-1l0-4z"; // broken lower chain link

  /**
   * append this to the {@link #CHAIN_LINK_BASE} to create the closed chains icon
   */
  public final static String CHAIN_CONNECTION_CLOSED =
    "M-1 5c0 1 1 1 1 1 1 0 1-1 1-1l0-10c0-1-1-1-1-1-1 0-1 1-1 1z"; // connecting chain link
}
