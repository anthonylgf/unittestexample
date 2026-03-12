CREATE TABLE public.aluno (
	id int8 NOT NULL,
	data_nascimento date NOT NULL,
	genero varchar(255) NOT NULL,
	nome_completo varchar(400) NOT NULL,
	CONSTRAINT aluno_genero_check CHECK (((genero)::text = ANY ((ARRAY['MASCULINO'::character varying, 'FEMININO'::character varying])::text[]))),
	CONSTRAINT aluno_pkey PRIMARY KEY (id),
	CONSTRAINT uk6nlyhj3esorpec0hpwd7kc9up UNIQUE (nome_completo)
);
