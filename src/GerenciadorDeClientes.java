import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;


public class GerenciadorDeClientes extends Thread {
	
	private Socket cliente;
	private String nomeCliente;
	private BufferedReader leitor;
	private PrintWriter escritor;
	private static final Map<String,GerenciadorDeClientes> clientes = new HashMap<String,GerenciadorDeClientes>();

	public GerenciadorDeClientes(Socket cliente) {
		this.cliente = cliente;
		start();
	}

	/**
	 * 
	 */
	@Override
	public void run() {
		try {
			leitor = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
			escritor = new PrintWriter(cliente.getOutputStream(), true);
			
			efetuarLogin();

				escritor.println("Digite SAIR para encerrar a aplicação ou " +
						"chat para conversar");
			String msg;
			while(true){
				msg = leitor.readLine();

				if(msg.equalsIgnoreCase(Comandos.SAIR)){
					this.cliente.close();
					escritor.println("usuário"+ this.cliente + "desconectado");
				}else if(msg.startsWith(Comandos.MENSAGEM)){
					escritor.println("Digite o nome do destinatário: ");
					msg = leitor.readLine();

					String nomeDestinario = msg;
					System.out.println("enviando para " + nomeDestinario);
					GerenciadorDeClientes destinario = clientes.get(nomeDestinario);
					escritor.println("destinatario"+destinario);
					if(destinario == null){
						escritor.println("O cliente informado nao existe");
					}else{
						escritor.println("digite uma mensagem para " + destinario.getNomeCliente());
						while(!msg.equalsIgnoreCase("quit")){
							destinario.getEscritor().println(this.nomeCliente + " disse: " + leitor.readLine());
						}
					}
					
				// lista o nome de todos os clientes logados
				}else if(msg.equals(Comandos.LISTA_USUARIOS)){
					atualizarListaUsuarios(this);


				}else{
					escritor.println(this.nomeCliente + ", você disse: " + msg);
				}
			}
			
		} catch (IOException e) {
			System.err.println("o cliente fechou a conexao");
			clientes.remove(this.nomeCliente);
			e.printStackTrace();
		}
	}

	private synchronized void  efetuarLogin() throws IOException {
		
		while(true){
			escritor.println(Comandos.LOGIN);
			this.nomeCliente = leitor.readLine().toLowerCase().replaceAll(",", "");
			if(this.nomeCliente.equalsIgnoreCase("null") || this.nomeCliente.isEmpty()){
				escritor.println(Comandos.LOGIN_NEGADO);
			}else if(clientes.containsKey(this.nomeCliente)){
				escritor.println(Comandos.LOGIN_NEGADO);
			}else{
				escritor.println(Comandos.LOGIN_ACEITO);
				escritor.println("olá " + this.nomeCliente);
				clientes.put(this.nomeCliente, this);

				for(String cliente: clientes.keySet()){
					atualizarListaUsuarios(clientes.get(cliente));
				}
				if (clientes.size() < 2){
					escritor.println("Ainda não existem clientes para conversar");
				}
				break;
			}
		}
	}

	private void atualizarListaUsuarios(GerenciadorDeClientes cliente) {
		StringBuffer str = new StringBuffer();
		for(String c: clientes.keySet()){
			if(cliente.getNomeCliente().equals(c))
				continue;
			
			str.append(c);
			str.append(",");
		}

		if(str.length() > 0)
			str.delete(str.length()-1, str.length());
		if(str.length() >1) {
			cliente.getEscritor().println("Cientes disponíveis para chat: " + str.toString());
		}

	}

	public PrintWriter getEscritor() {
		return escritor;
	}

	public String getNomeCliente() {
		return nomeCliente;
	}
	
//	public BufferedReader getLeitor() {
//		return leitor;
//	}
}
