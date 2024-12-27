DROP table if exists public.tb_config ;

CREATE TABLE public.tb_config (
	id int2 NOT NULL,
	last_person_update_time timestamp NULL,
	update_tag varchar NULL
);


-- Column comments

COMMENT ON COLUMN public.tb_config.last_person_update_time IS '记录的上一次人员表updateTime,做增量查询';

-- Permissions

ALTER TABLE public.tb_config OWNER TO postgres;
GRANT ALL ON TABLE public.tb_config TO postgres;
GRANT ALL ON TABLE public.tb_config TO sync;

INSERT INTO public.tb_config
(id, last_person_update_time, update_tag)
VALUES(1, '2024-11-01 13:12:39.237', '7c23a192-62d4-4b6b-b427-bb758b0fffd2');

DROP table if exists  public.tb_person_pic;

CREATE TABLE public.tb_person_pic (
	pic_data varchar NULL, -- 人员照片Base64
	update_time timestamp NULL, -- 更新时间
	id varchar NOT NULL, -- 人员id
	face_id varchar NULL -- 平台上的照片id
);

-- Column comments

COMMENT ON COLUMN public.tb_person_pic.pic_data IS '人员照片Base64';
COMMENT ON COLUMN public.tb_person_pic.update_time IS '更新时间';
COMMENT ON COLUMN public.tb_person_pic.id IS '人员id';
COMMENT ON COLUMN public.tb_person_pic.face_id IS '平台上的照片id';

-- Permissions

ALTER TABLE public.tb_person_pic OWNER TO postgres;
GRANT ALL ON TABLE public.tb_person_pic TO postgres;
GRANT ALL ON TABLE public.tb_person_pic TO sync;


DROP table if exists  public.tb_sync_plat_org;

CREATE TABLE public.tb_sync_plat_org (
	id varchar NOT NULL,
	org_name varchar NULL,
	parent_id varchar NULL,
	CONSTRAINT tb_sync_plat_org_pkey PRIMARY KEY (id)
);

-- Permissions

ALTER TABLE public.tb_sync_plat_org OWNER TO postgres;
GRANT ALL ON TABLE public.tb_sync_plat_org TO postgres;
GRANT ALL ON TABLE public.tb_sync_plat_org TO sync;


DROP table if exists  public.tb_person_infos;

CREATE TABLE public.tb_person_info (
	id varchar NOT NULL, -- 人员唯一标识,工号
	person_name varchar NULL, -- 人员名称
	org_id varchar NULL, -- 人员所属组织id
	org_name varchar NULL, -- 人员所属组织名称
	update_time timestamp NULL, -- 记录更新时间
	CONSTRAINT tb_sync_plat_person_pkey PRIMARY KEY (id)
);

-- Column comments

COMMENT ON COLUMN public.tb_person_info.id IS '人员唯一标识,工号';
COMMENT ON COLUMN public.tb_person_info.person_name IS '人员名称';
COMMENT ON COLUMN public.tb_person_info.org_id IS '人员所属组织id';
COMMENT ON COLUMN public.tb_person_info.org_name IS '人员所属组织名称';
COMMENT ON COLUMN public.tb_person_info.update_time IS '记录更新时间';

-- Permissions

ALTER TABLE public.tb_person_info OWNER TO postgres;
GRANT ALL ON TABLE public.tb_person_info TO postgres;
GRANT ALL ON TABLE public.tb_person_info TO sync;

DROP TABLE  if exists public.tb_middle_person_info;

CREATE TABLE public.tb_middle_person_info (
	id varchar NOT NULL, -- 人员唯一标识,工号
	person_name varchar NULL, -- 人员名称
	org_id varchar NULL, -- 人员所属组织id
	org_name varchar NULL, -- 人员所属组织名称
	update_time timestamp NULL, -- 记录更新时间
	update_tag varchar NULL  -- 人员更新标记
);

-- Column comments

COMMENT ON COLUMN public.tb_middle_person_info.id IS '人员唯一标识,工号';
COMMENT ON COLUMN public.tb_middle_person_info.person_name IS '人员名称';
COMMENT ON COLUMN public.tb_middle_person_info.org_id IS '人员所属组织id';
COMMENT ON COLUMN public.tb_middle_person_info.org_name IS '人员所属组织名称';
COMMENT ON COLUMN public.tb_middle_person_info.update_time IS '记录更新时间';
COMMENT ON COLUMN public.tb_middle_person_info.update_tag IS '人员更新标记';

-- Permissions

ALTER TABLE public.tb_middle_person_info OWNER TO postgres;
GRANT ALL ON TABLE public.tb_middle_person_info TO postgres;
GRANT ALL ON TABLE public.tb_middle_person_info TO sync;


 DROP TABLE  if exists  public.tb_card_info;

CREATE TABLE public.tb_card_info (
	zh varchar NULL, -- 账号
	xgh varchar NULL, -- 学工号，和人员表的id对应
	kh varchar NULL, -- 卡号
	klx varchar NULL -- 卡类型
);

-- Column comments

COMMENT ON COLUMN public.tb_card_info.zh IS '账号';
COMMENT ON COLUMN public.tb_card_info.xgh IS '学工号';
COMMENT ON COLUMN public.tb_card_info.kh IS '卡号';
COMMENT ON COLUMN public.tb_card_info.klx IS '卡类型';

-- Permissions

ALTER TABLE public.tb_card_info OWNER TO postgres;
GRANT ALL ON TABLE public.tb_card_info TO postgres;
GRANT ALL ON TABLE public.tb_card_info TO sync;