package xxmilp;
public class Amemory {
public static void main(String [] args) {
	
	int mb = 1024*1024;
	
	//Getting the runtime reference from system
	Runtime runtime = Runtime.getRuntime();
	/*aggiunto commento amemori e altro*/
	  
	
	System.out.println("##### Heap utilization statistics [MB] #####");
	
	//Print used memory
	System.out.println("Used Memory:" 
		+ (runtime.totalMemory() - runtime.freeMemory()) / mb);

	//Print free memory
	System.out.println("Free Memory:" 
		+ runtime.freeMemory() / mb); 
	
	//Print total available memory
	/*
	Restituisce la quantita totale di memoria nella Java virtual machine. 
	Il valore restituito da questo metodo puo variare nel tempo, a seconda dell'ambiente host. 
	Si noti che la quantita di memoria richiesta per contenere un oggetto di un 
	determinato tipo puo dipendere dall'implementazione.
	Fatta
	 * 
	 */
	
	System.out.println("Total Memory:" + runtime.totalMemory() / mb);

	//Print Maximum available memory
	/*
	 * Restituisce la quantita massima di memoria che la macchina virtuale Java tentera 
	 * di utilizzare. Se non esiste un limite inerente, verra restituito il valore 
	 * Long.MAX_VALUE.
	 */
	System.out.println("Max Memory:" + runtime.maxMemory() / mb);
}
}
