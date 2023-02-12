package Models;

public class LocalState {
    public int lowerHeadingBase;
    public int higherHeadingBase;
    public boolean specialSector;

    public boolean teleporterFired = false;
    public boolean teleporterStillNotAppear = true;
    public int teleporterHeading;
    
    public int tpReason=0; // 0 : belum ada teleporter
                          // 1 : makan enemy yang lebih kecil
                          // 2 : kabur dari enemy yang lebih besar/torpedo
}
