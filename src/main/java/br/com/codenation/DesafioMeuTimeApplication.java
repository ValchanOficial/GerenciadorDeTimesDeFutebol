package br.com.codenation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import br.com.codenation.desafio.annotation.Desafio;
import br.com.codenation.desafio.app.MeuTimeInterface;
import br.com.codenation.desafio.exceptions.CapitaoNaoInformadoException;
import br.com.codenation.desafio.exceptions.IdentificadorUtilizadoException;
import br.com.codenation.desafio.exceptions.JogadorNaoEncontradoException;
import br.com.codenation.desafio.exceptions.TimeNaoEncontradoException;

public class DesafioMeuTimeApplication implements MeuTimeInterface {
	private final Map<Long, Time> timesCadastrados = new HashMap<>();
	private final Map<Long, Jogador> jogadoresCadastrados = new HashMap<>();

	private final Optional<Time> buscaTimePorId(Long id) {
		return Optional.ofNullable(timesCadastrados.get(id));
	}

	private final Optional<Jogador> buscaJogadorPorId(Long id) {
		return Optional.ofNullable(jogadoresCadastrados.get(id));
	}

	private final List<Time> getListaTimes() {
		return new ArrayList<>(timesCadastrados.values());
	}

	private final List<Jogador> getListaJogadores() {
		return new ArrayList<>(jogadoresCadastrados.values());
	}

	private final void incluirTime(Time time) {
		timesCadastrados.put(time.getId(), time);
	}

	private final void incluirJogador(Jogador jogador) {
		jogadoresCadastrados.put(jogador.getId(), jogador);
	}

	@Desafio("incluirTime")
	public void incluirTime(Long id, String nome, LocalDate dataCriacao, String corUniformePrincipal, String corUniformeSecundario) {
		boolean timeJaExiste = buscaTimePorId(id).isPresent();
		if (timeJaExiste) {
			throw new IdentificadorUtilizadoException();
		}
		incluirTime(new Time(id, nome, dataCriacao, corUniformePrincipal, corUniformeSecundario));
	}

	@Desafio("incluirJogador")
	public void incluirJogador(Long id, Long idTime, String nome, LocalDate dataNascimento, Integer nivelHabilidade, BigDecimal salario) {
		boolean jogadorJaExiste = buscaJogadorPorId(id).isPresent();
		if (jogadorJaExiste) {
			throw new IdentificadorUtilizadoException();
		}
		Optional<Time> timeOptional = buscaTimePorId(idTime);
		boolean timeNaoExiste = !timeOptional.isPresent();
		if (timeNaoExiste) {
			throw new TimeNaoEncontradoException();
		}
		incluirJogador(new Jogador(id, timeOptional.get(), nome, dataNascimento, nivelHabilidade, salario));
	}

	@Desafio("definirCapitao")
	public void definirCapitao(Long idJogador) {
		Optional<Jogador> jogadorOptional = buscaJogadorPorId(idJogador);
		boolean jogadorNaoExiste = !jogadorOptional.isPresent();
		if (jogadorNaoExiste) {
			throw new JogadorNaoEncontradoException();
		}
		Jogador jogador = jogadorOptional.get();
		jogador.getTime().setCapitao(jogador);
	}

	@Desafio("buscarCapitaoDoTime")
	public Long buscarCapitaoDoTime(Long idTime) {
		Optional<Time> timeOptional = buscaTimePorId(idTime);
		boolean timeNaoExiste = !timeOptional.isPresent();
		if (timeNaoExiste) {
			throw new TimeNaoEncontradoException();
		}
		Time time = timeOptional.get();
		Optional<Jogador> capitaoOptional = Optional.ofNullable(time.getCapitao());
		boolean timeNaoTemCapitao = !capitaoOptional.isPresent();
		if (timeNaoTemCapitao) {
			throw new CapitaoNaoInformadoException();
		}
		Jogador capitao = capitaoOptional.get();
		return capitao.getId();
	}

	@Desafio("buscarNomeJogador")
	public String buscarNomeJogador(Long idJogador) {
		Optional<Jogador> jogadorOptional = buscaJogadorPorId(idJogador);
		boolean jogadorNaoExiste = !jogadorOptional.isPresent();
		if (jogadorNaoExiste) {
			throw new JogadorNaoEncontradoException();
		}
		Jogador jogador = jogadorOptional.get();
		return jogador.getNome();
	}

	@Desafio("buscarNomeTime")
	public String buscarNomeTime(Long idTime) {
		Optional<Time> timeOptional = buscaTimePorId(idTime);
		boolean timeNaoExiste = !timeOptional.isPresent();
		if (timeNaoExiste) {
			throw new TimeNaoEncontradoException();
		}
		Time time = timeOptional.get();
		return time.getNome();
	}

	@Desafio("buscarJogadoresDoTime")
	public List<Long> buscarJogadoresDoTime(Long idTime) {
		Optional<Time> timeOptional = buscaTimePorId(idTime);
		boolean timeNaoExiste = !timeOptional.isPresent();
		if (timeNaoExiste) {
			throw new TimeNaoEncontradoException();
		}
		Time timeProcurado = timeOptional.get();
		return getListaJogadores().parallelStream()
				.filter(jogadores -> jogadores.getTime().equals(timeProcurado))
				.map(Jogador::getId)
				.sorted()
				.collect(Collectors.toList());
	}

	@Desafio("buscarMelhorJogadorDoTime")
	public Long buscarMelhorJogadorDoTime(Long idTime) {
		Optional<Time> timeOptional = buscaTimePorId(idTime);
		boolean timeNaoExiste = !timeOptional.isPresent();
		if (timeNaoExiste) {
			throw new TimeNaoEncontradoException();
		}
		Time timeProcurado = timeOptional.get();
		return getListaJogadores().parallelStream()
				.filter(jogadores -> jogadores.getTime().equals(timeProcurado))
				.max(Comparator.comparingInt(Jogador::getNivelHabilidade))
				.get()
				.getId();
	}

	@Desafio("buscarJogadorMaisVelho")
	public Long buscarJogadorMaisVelho(Long idTime) {
		Optional<Time> timeOptional = buscaTimePorId(idTime);
		boolean timeNaoExiste = !timeOptional.isPresent();
		if (timeNaoExiste) {
			throw new TimeNaoEncontradoException();
		}
		Time timeProcurado = timeOptional.get();
		return getListaJogadores().parallelStream()
				.filter(jogadores -> jogadores.getTime().equals(timeProcurado))
				.sorted(Comparator.comparingLong(Jogador::getId))
				.min(Comparator.comparing(Jogador::getDataNascimento))
				.get()
				.getId();
	}

	@Desafio("buscarTimes")
	public List<Long> buscarTimes() {
		return getListaTimes().parallelStream()
				.map(Time::getId)
				.sorted()
				.collect(Collectors.toList());
	}

	@Desafio("buscarJogadorMaiorSalario")
	public Long buscarJogadorMaiorSalario(Long idTime) {
		Optional<Time> timeOptional = buscaTimePorId(idTime);
		boolean timeNaoExiste = !timeOptional.isPresent();
		if (timeNaoExiste) {
			throw new TimeNaoEncontradoException();
		}
		Time timeProcurado = timeOptional.get();
		return getListaJogadores().parallelStream()
				.filter(jogadores -> jogadores.getTime().equals(timeProcurado))
				.sorted(Comparator.comparingLong(Jogador::getId))
				.max(Comparator.comparing(Jogador::getSalario))
				.get()
				.getId();
	}

	@Desafio("buscarSalarioDoJogador")
	public BigDecimal buscarSalarioDoJogador(Long idJogador) {
		Optional<Jogador> jogadorOptional = buscaJogadorPorId(idJogador);
		boolean jogadorNaoExiste = !jogadorOptional.isPresent();
		if (jogadorNaoExiste) {
			throw new JogadorNaoEncontradoException();
		}
		Jogador jogador = jogadorOptional.get();
		return jogador.getSalario();
	}

	@Desafio("buscarTopJogadores")
	public List<Long> buscarTopJogadores(Integer top) {
		return getListaJogadores().parallelStream()
				.sorted(Comparator.comparingLong(Jogador::getId))
				.sorted(Comparator.comparingInt(Jogador::getNivelHabilidade).reversed())
				.limit(top)
				.map(Jogador::getId)
				.collect(Collectors.toList());
	}

	@Desafio("buscarCorCamisaTimeDeFora")
	public String buscarCorCamisaTimeDeFora(Long timeDaCasa, Long timeDeFora) {
		Optional<Time> timeCasaOptional = buscaTimePorId(timeDaCasa);
		Optional<Time> timeForaOptional = buscaTimePorId(timeDeFora);
		boolean algumDosTimesNaoExiste = !timeCasaOptional.isPresent() || !timeForaOptional.isPresent();
		if (algumDosTimesNaoExiste) {
			throw new TimeNaoEncontradoException();
		}
		Time timeCasa = timeCasaOptional.get();
		String timeCasaUniforme = timeCasa.getCorUniformePrincipal();
		Time timeFora = timeForaOptional.get();
		String timeForaUniforme = timeFora.getCorUniformePrincipal();
		if (timeForaUniforme.equals(timeCasaUniforme)) {
			timeForaUniforme = timeFora.getCorUniformeSecundario();
		}
		return timeForaUniforme;
	}
}