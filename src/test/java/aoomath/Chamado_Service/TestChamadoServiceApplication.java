package aoomath.Chamado_Service;

import org.springframework.boot.SpringApplication;

public class TestChamadoServiceApplication {

	public static void main(String[] args) {
		SpringApplication.from(ChamadoServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
