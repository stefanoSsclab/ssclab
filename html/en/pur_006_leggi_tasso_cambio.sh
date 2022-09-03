NOW=$(date +"%Y.%m.%d_%H.%M.%S")
NOWT=$(date +"%Y%m%d_%H_%M")
SERVERNAME=$(hostname)
NOMEJOB=pur_006_leggi_tasso_cambio
OS=$(uname)
echo "Your OS : $OS"
echo "Server Name : $SERVERNAME"
echo "----------------------"
echo "----------------------"
echo "INIZIO : $NOW"
echo "Job : $NOMEJOB"
echo "----------------------"
echo "----------------------"
prjdir=/sasgrid/omnia/coll
/sas/software/SASFoundation/9.4/bin/sas_u8 -autoexec $prjdir/autoexec_omnia.sas -sysin $prjdir/be/pgm/$NOMEJOB.sas -nonews -log $prjdir/logs/be/$NOMEJOB'_'$NOWT.log
retcode=$?
NOW=$(date +"%Y.%m.%d_%H.%M.%S")
echo "FINE : $NOW"
echo exit code: $retcode
echo "----------------------"
echo "----------------------"
exit $retcode